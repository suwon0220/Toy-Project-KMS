package toy.kms.keyprovider.local;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.keyprovider.adapter.KeyGenerator;
import toy.kms.keyprovider.adapter.Signer;
import toy.kms.application.exception.KeyMaterialProviderException;
import toy.kms.application.model.GeneratedKeyMaterial;
import toy.kms.application.port.KeyProviderService;
import toy.kms.domain.key.KeyRef;
import toy.kms.domain.key.KeyProviderId;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.signature.SignatureParameters;
import toy.kms.keyprovider.local.domain.ProvidedKey;
import toy.kms.keyprovider.local.jca.keyref.KeyRefGenerator;
import toy.kms.keyprovider.local.jca.persistence.ProviderKeyRepository;
import toy.kms.keyprovider.local.jca.registry.JcaKeyGeneratorRegistry;
import toy.kms.keyprovider.local.jca.registry.JcaSignerRegistry;

import java.security.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LocalKeyProviderService implements KeyProviderService {

    private static final KeyProviderId PROVIDER_ID = new KeyProviderId("LocalKeyProvider");
    private final JcaKeyGeneratorRegistry jcaKeyGeneratorRegistry;
    private final JcaSignerRegistry jcaSignerRegistry;

    private final ProviderKeyRepository providerKeyRepository;
    private final KeyRefGenerator keyRefGenerator;

    @Override
    public KeyProviderId id() {
        return PROVIDER_ID;
    }

    @Override
    public GeneratedKeyMaterial generate(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPair keyPair = generateKeyPair(parameters);

        ProvidedKey providedKey = new ProvidedKey(
                keyRefGenerator.generateKeyRef(keyPair.getPublic()),
                parameters.toString(),
                keyPair
        );

        providerKeyRepository.save(providedKey);
        return GeneratedKeyMaterial.from(providedKey);
    }

    @Override
    public Optional<PublicKey> getPublicKey(KeyRef reference) {
        return providerKeyRepository.findById(reference)
                .map(ProvidedKey::getKeyPair)
                .map(KeyPair::getPublic);
    }

    @Override
    public byte[] sign(
            @Nonnull KeyRef reference,
            @Nonnull SignatureParameters parameters,
            @Nonnull byte[] data) {
        return providerKeyRepository.findById(reference)
                .map(providedKey -> {
                    try {
                        Signer signer = jcaSignerRegistry.get(parameters);
                        return signer.sign(providedKey.getKeyPair().getPrivate(), parameters, data);
                    } catch (GeneralSecurityException | ProviderException e) {
                        throw new KeyMaterialProviderException(
                                "Failed to sign data",
                                e);
                    }
                })
                .orElseThrow(() -> new NoSuchElementException(
                        "Key material not found: " + reference.value()));
    }

    @Override
    public boolean verify(
            @Nonnull KeyRef reference,
            @Nonnull SignatureParameters parameters,
            @Nonnull byte[] data,
            @Nonnull byte[] signature) {
        return providerKeyRepository.findById(reference)
                .map(providedKey -> {
                    try {
                        Signer signer = jcaSignerRegistry.get(parameters);
                        return signer.verify(providedKey.getKeyPair().getPublic(), parameters, data, signature);
                    } catch (GeneralSecurityException | ProviderException e) {
                        throw new KeyMaterialProviderException(
                                "Failed to verify signature",
                                e);
                    }
                })
                .orElseThrow(() -> new NoSuchElementException(
                        "Key material not found: " + reference.value()));
    }

    @Override
    public void delete(KeyRef reference) {
        providerKeyRepository.delete(reference);
    }

    // ----------------------------------------------

    private KeyPair generateKeyPair(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = jcaKeyGeneratorRegistry.get(parameters);
        return keyGenerator.generate(parameters);
    }
}
