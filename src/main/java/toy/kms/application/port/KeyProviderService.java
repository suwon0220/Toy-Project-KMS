package toy.kms.application.port;

import jakarta.transaction.Transactional;
import toy.kms.application.model.GeneratedKeyMaterial;
import toy.kms.domain.key.KeyRef;
import toy.kms.domain.key.KeyProviderId;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.signature.SignatureParameters;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Optional;

public interface KeyProviderService {

    KeyProviderId id();

    @Transactional
    GeneratedKeyMaterial generate(
            KeyGenerationParameters parameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

    Optional<PublicKey> getPublicKey(
            KeyRef reference);

    byte[] sign(
            KeyRef reference,
            SignatureParameters parameters,
            byte[] data);

    boolean verify(
            KeyRef reference,
            SignatureParameters parameters,
            byte[] data,
            byte[] signature);

    @Transactional
    void delete(
            KeyRef reference);

}