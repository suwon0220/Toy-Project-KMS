package toy.kms.keyprovider.adapter;

import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.key.KeyRef;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public interface KeyGenerator {
    boolean supports(KeyGenerationParameters parameters);

    KeyPair generate(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
}
