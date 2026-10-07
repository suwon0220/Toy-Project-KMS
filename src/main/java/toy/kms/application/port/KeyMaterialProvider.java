package toy.kms.application.port;

import toy.kms.application.model.GeneratedKeyMaterial;
import toy.kms.domain.KeyMaterialRef;
import toy.kms.domain.KeyProviderId;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.signature.SignatureParameters;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Optional;

public interface KeyMaterialProvider {

    KeyProviderId id();

    GeneratedKeyMaterial generate(
            KeyGenerationParameters parameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

    Optional<PublicKey> getPublicKey(
            KeyMaterialRef reference);

    byte[] sign(
            KeyMaterialRef reference,
            SignatureParameters parameters,
            byte[] data);

    boolean verify(
            KeyMaterialRef reference,
            SignatureParameters parameters,
            byte[] data,
            byte[] signature);

    void delete(
            KeyMaterialRef reference);

}