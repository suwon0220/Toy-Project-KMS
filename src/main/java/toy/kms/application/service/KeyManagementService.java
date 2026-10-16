package toy.kms.application.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import toy.kms.application.exception.ManagedKeyNotFoundException;
import toy.kms.application.model.GeneratedKeyMaterial;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.port.ManagedKeyIdGenerator;
import toy.kms.application.port.KeyProviderService;
import toy.kms.application.port.ManagedKeyRepository;
import toy.kms.application.registry.KeyProviderRegistry;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.application.web.KeyAlgorithmPreset;
import toy.kms.domain.key.KeyProviderId;
import toy.kms.domain.key.ManagedKey;
import toy.kms.domain.key.ManagedKeyId;
import toy.kms.domain.signature.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class KeyManagementService {

    private final ManagedKeyRepository managedKeyRepository;
    private final ManagedKeyIdGenerator managedKeyIdGenerator;
    private final KeyProviderRegistry keyProviderRegistry;

    public Optional<ManagedKey> findById(@NotNull ManagedKeyId managedKeyId) {
        return managedKeyRepository.findById(managedKeyId);
    }

    public List<ManagedKey> findAll(KeySearchCriteria criteria) {
        return managedKeyRepository.findAll(criteria);
    }

    public ManagedKey generate(@NotEmpty String providerId, @NotNull String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return generate(providerId, keyAlgorithm, null);
    }

    public ManagedKey generate(
            @NotEmpty String providerId,
            @NotNull String keyAlgorithm,
            String alias)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyAlgorithmPreset preset = KeyAlgorithmPreset.fromString(keyAlgorithm);
        KeyGenerationParameters keyGenerationParameters = preset.toParameters();
        log.debug("Generating key with providerId: {}, parameters: {}", providerId, keyGenerationParameters);

        // KeyMaterialProvider 조회
        KeyProviderId keyProviderId = new KeyProviderId(providerId);
        KeyProviderService keyProviderService = keyProviderRegistry.get(keyProviderId);

        // Provider에게 키 생성 요청
        GeneratedKeyMaterial generatedKeyMaterial = keyProviderService.generate(keyGenerationParameters);
        if (generatedKeyMaterial == null || generatedKeyMaterial.reference() == null) {
            throw new IllegalStateException("Generated key material is null or has no reference");
        }

        // Provider에서 키를 생성하고 reference를 검증한 다음 실행
        try {
            ManagedKey managedKey = new ManagedKey(
                    managedKeyIdGenerator.generate(generatedKeyMaterial.publicKey()),
                    Instant.now(),
                    alias,
                    preset,
                    generatedKeyMaterial.reference(),
                    keyProviderId
                    );

            managedKeyRepository.save(managedKey);
            log.debug("Managed key created and saved: {}", managedKey);
            return managedKey;
        } catch (RuntimeException failure) {
            try {
                keyProviderService.delete(generatedKeyMaterial.reference());
            } catch (RuntimeException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
                log.error(
                        "Failed to clean up key material: providerId={}, reference={}",
                        keyProviderId,
                        generatedKeyMaterial.reference(),
                        cleanupFailure);
            }

            throw failure;
        }
    }

    public byte[] sign(
            ManagedKeyId managedKeyId,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm,
            byte[] data) {
        ManagedKey managedKey = managedKeyRepository.findById(managedKeyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(managedKeyId));

        KeyProviderService provider = keyProviderRegistry.get(
                managedKey.getKeyProviderId());

        SignatureParameters signatureParameters = getSignatureParameters(signatureAlgorithm, digestAlgorithm);

        return provider.sign(
                managedKey.getKeyRef(),
                signatureParameters,
                data);
    }

    public boolean verify(
            ManagedKeyId managedKeyId,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm,
            byte[] data,
            byte[] signature) {
        ManagedKey managedKey = managedKeyRepository.findById(managedKeyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(managedKeyId));

        KeyProviderService provider = keyProviderRegistry.get(
                managedKey.getKeyProviderId());

        // Check if the Key supports the requested signature and digest algorithms
        SignatureParameters signatureParameters = getSignatureParameters(signatureAlgorithm, digestAlgorithm);

        return provider.verify(
                managedKey.getKeyRef(),
                signatureParameters,
                data,
                signature);
    }

    public ManagedKeyId delete(ManagedKeyId managedKeyId) {
        ManagedKey managedKey = managedKeyRepository.findById(managedKeyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(managedKeyId));

        KeyProviderService provider = keyProviderRegistry.get(
                managedKey.getKeyProviderId());

        provider.delete(managedKey.getKeyRef());
        managedKeyRepository.delete(managedKeyId);
        return managedKeyId;
    }

    private @NonNull SignatureParameters getSignatureParameters(SignatureAlgorithm signatureAlgorithm, DigestAlgorithm digestAlgorithm) {
        return switch (signatureAlgorithm) {
            case ECDSA -> new EcdsaSignatureParameters(digestAlgorithm);
            case Ed448 -> new Ed448SignatureParameters();
            case Ed25519 -> new Ed25519SignatureParameters();
            case RSA_PKCS1_V1_5 -> new RsaPkcs1SignatureParameters(digestAlgorithm);
            case RSA_PSS -> throw new UnsupportedOperationException("RSA_PSS is not supported yet");
            default -> throw new IllegalArgumentException("Unsupported signature algorithm: " + signatureAlgorithm);
        };
    }

    public byte[] getPublicKeyOf(ManagedKeyId managedKeyId) {
        ManagedKey managedKey = managedKeyRepository.findById(managedKeyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(managedKeyId));

        KeyProviderService provider = keyProviderRegistry.get(
                managedKey.getKeyProviderId());

        PublicKey publicKey = provider.getPublicKey(managedKey.getKeyRef())
                .orElseThrow(() -> new IllegalStateException("Public key not found for keyId: " + managedKeyId));

        return publicKey.getEncoded();
    }
}
