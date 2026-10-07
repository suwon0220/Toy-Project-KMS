package toy.kms.application.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import toy.kms.application.exception.ManagedKeyNotFoundException;
import toy.kms.application.model.GeneratedKeyMaterial;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.port.KeyIdGenerator;
import toy.kms.application.port.KeyMaterialProvider;
import toy.kms.application.port.KeyRepository;
import toy.kms.application.registry.KeyMaterialProviderRegistry;
import toy.kms.domain.*;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.signature.*;
import toy.kms.web.KeyAlgorithmPreset;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class KeyManagementService {

    private final KeyRepository keyRepository;
    private final KeyIdGenerator keyIdGenerator;
    private final KeyMaterialProviderRegistry keyMaterialProviderRegistry;

    public List<ManagedKey> search(@NotNull KeySearchCriteria criteria) {
        return keyRepository.findByCriteria(criteria);
    }

    public Optional<ManagedKey> findById(@NotNull KeyId keyId) {
        return keyRepository.findById(keyId);
    }

    public List<ManagedKey> findAll() {
        return keyRepository.findAll();
    }

    public ManagedKey generate(@NotEmpty String providerId, @NotNull String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return generate(providerId, keyAlgorithm, null);
    }

    public ManagedKey generate(
            @NotEmpty String providerId,
            @NotNull String keyAlgorithm,
            @Size(max = ManagedKey.MAX_ALIAS_LENGTH) String alias)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyAlgorithmPreset preset = KeyAlgorithmPreset.fromString(keyAlgorithm);
        KeyGenerationParameters keyGenerationParameters = preset.toParameters();
        log.debug("Generating key with providerId: {}, parameters: {}", providerId, keyGenerationParameters);

        // KeyMaterialProvider 조회
        KeyProviderId keyProviderId = new KeyProviderId(providerId);
        KeyMaterialProvider keyMaterialProvider = keyMaterialProviderRegistry.get(keyProviderId);

        // Provider에게 키 생성 요청
        GeneratedKeyMaterial generatedKeyMaterial = keyMaterialProvider.generate(keyGenerationParameters);
        if (generatedKeyMaterial == null || generatedKeyMaterial.reference() == null) {
            throw new IllegalStateException("Generated key material is null or has no reference");
        }

        // Provider에서 키를 생성하고 reference를 검증한 다음 실행
        try {
            ManagedKey managedKey = new ManagedKey(
                    keyIdGenerator.generate(generatedKeyMaterial.publicKey()),
                    preset,
                    generatedKeyMaterial.reference(),
                    keyProviderId,
                    java.time.Instant.now());
            managedKey.setAlias(alias);

            keyRepository.save(managedKey);
            log.debug("Managed key created and saved: {}", managedKey);
            return managedKey;
        } catch (RuntimeException failure) {
            try {
                keyMaterialProvider.delete(generatedKeyMaterial.reference());
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
            KeyId keyId,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm,
            byte[] data) {
        ManagedKey managedKey = keyRepository.findById(keyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(keyId));

        KeyMaterialProvider provider = keyMaterialProviderRegistry.get(
                managedKey.getKeyProviderId());

        SignatureParameters signatureParameters = getSignatureParameters(signatureAlgorithm, digestAlgorithm);

        return provider.sign(
                managedKey.getKeyMaterialRef(),
                signatureParameters,
                data);
    }

    public boolean verify(
            KeyId keyId,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm,
            byte[] data,
            byte[] signature) {
        ManagedKey managedKey = keyRepository.findById(keyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(keyId));

        KeyMaterialProvider provider = keyMaterialProviderRegistry.get(
                managedKey.getKeyProviderId());

        // Check if the Key supports the requested signature and digest algorithms
        SignatureParameters signatureParameters = getSignatureParameters(signatureAlgorithm, digestAlgorithm);

        return provider.verify(
                managedKey.getKeyMaterialRef(),
                signatureParameters,
                data,
                signature);
    }

    public KeyId delete(KeyId keyId) {
        ManagedKey managedKey = keyRepository.findById(keyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(keyId));

        KeyMaterialProvider provider = keyMaterialProviderRegistry.get(
                managedKey.getKeyProviderId());

        provider.delete(managedKey.getKeyMaterialRef());
        keyRepository.delete(keyId);
        return keyId;
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

    public byte[] getPublicKeyOf(KeyId keyId) {
        ManagedKey managedKey = keyRepository.findById(keyId)
                .orElseThrow(() -> new ManagedKeyNotFoundException(keyId));

        KeyMaterialProvider provider = keyMaterialProviderRegistry.get(
                managedKey.getKeyProviderId());

        PublicKey publicKey = provider.getPublicKey(managedKey.getKeyMaterialRef())
                .orElseThrow(() -> new IllegalStateException("Public key not found for keyId: " + keyId));

        return publicKey.getEncoded();
    }
}
