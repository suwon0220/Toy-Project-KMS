package toy.kms.adapter.keymaterial.memory;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.adapter.keymaterial.jca.JcaKeyGenerator;
import toy.kms.adapter.keymaterial.jca.JcaSignatureOperator;
import toy.kms.application.exception.KeyMaterialProviderException;
import toy.kms.application.model.GeneratedKeyMaterial;
import toy.kms.application.port.KeyMaterialProvider;
import toy.kms.domain.KeyMaterialRef;
import toy.kms.domain.KeyProviderId;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.signature.SignatureParameters;
import toy.kms.infrastructure.jca.keygenerator.JcaKeyGeneratorRegistry;
import toy.kms.infrastructure.jca.signer.JcaSignatureOperatorRegistry;

import java.security.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class InMemoryKeyMaterialProvider implements KeyMaterialProvider {

    private static final KeyProviderId PROVIDER_ID = new KeyProviderId("in-memory");
    private final Map<KeyMaterialRef, KeyPair> store = new ConcurrentHashMap<>();
    private final JcaKeyGeneratorRegistry jcaKeyGeneratorRegistry;
    private final JcaSignatureOperatorRegistry jcaSignerRegistry;

    @Override
    public KeyProviderId id() {
        return PROVIDER_ID;
    }

    @Override
    public GeneratedKeyMaterial generate(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPair keyPair = generateKeyPair(parameters);

        // TODO: Use a proper KeyId generation strategy based on the public key
        KeyMaterialRef reference = new KeyMaterialRef(UUID.randomUUID().toString());

        if (store.putIfAbsent(reference, keyPair) != null) {
            throw new IllegalStateException("Key with reference " + reference + " already exists");
        }

        return new GeneratedKeyMaterial(reference, keyPair.getPublic());
    }

    @Override
    public Optional<PublicKey> getPublicKey(KeyMaterialRef reference) {
        return Optional.ofNullable(store.get(reference)).map(KeyPair::getPublic);
    }

    @Override
    public byte[] sign(
            @Nonnull KeyMaterialRef reference,
            @Nonnull SignatureParameters parameters,
            @Nonnull byte[] data) {
        KeyPair keyPair = store.get(reference);
        if (keyPair == null) {
            throw new NoSuchElementException(
                    "Key material not found: " + reference.value());
        }

        JcaSignatureOperator signer = jcaSignerRegistry.get(parameters);

        try {
            return signer.sign(keyPair.getPrivate(), parameters, data);
        } catch (GeneralSecurityException | ProviderException e) {
            throw new KeyMaterialProviderException(
                    "Failed to sign data",
                    e);
        }
    }

    @Override
    public boolean verify(
            @Nonnull KeyMaterialRef reference,
            @Nonnull SignatureParameters parameters,
            @Nonnull byte[] data,
            @Nonnull byte[] signature) {
        KeyPair keyPair = store.get(reference);
        if (keyPair == null) {
            throw new NoSuchElementException(
                    "Key material not found: " + reference.value());
        }

        JcaSignatureOperator signer = jcaSignerRegistry.get(parameters);

        try {
            return signer.verify(keyPair.getPublic(), parameters, data, signature);
        } catch (GeneralSecurityException | ProviderException e) {
            throw new KeyMaterialProviderException(
                    "Failed to verify signature",
                    e);
        }
    }

    @Override
    public void delete(KeyMaterialRef reference) {
        store.remove(reference);
    }

    // ----------------------------------------------

    private KeyPair generateKeyPair(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        JcaKeyGenerator keyGenerator = jcaKeyGeneratorRegistry.get(parameters);
        return keyGenerator.generate(parameters);
    }
}
