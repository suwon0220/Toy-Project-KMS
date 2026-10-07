package toy.kms.adapter.keymaterial.jca;

import toy.kms.domain.generation.KeyGenerationParameters;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public interface JcaKeyGenerator {
    boolean supports(KeyGenerationParameters parameters);

    KeyPair generate(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
}
