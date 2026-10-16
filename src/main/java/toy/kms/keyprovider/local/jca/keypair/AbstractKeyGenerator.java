package toy.kms.keyprovider.local.jca.keypair;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import toy.kms.domain.key.KeyRef;
import toy.kms.keyprovider.adapter.KeyGenerator;
import toy.kms.domain.generation.KeyGenerationParameters;

import java.security.*;

@RequiredArgsConstructor
public abstract class AbstractKeyGenerator implements KeyGenerator {

    @Autowired
    private Provider provider;

    @Override
    public KeyPair generate(KeyGenerationParameters parameters)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(parameters.algorithm().getJcaName(), provider);
        initializeKeyPairGenerator(keyPairGenerator, parameters);
        return keyPairGenerator.generateKeyPair();
    }

    protected abstract void initializeKeyPairGenerator(
            KeyPairGenerator keyPairGenerator,
            KeyGenerationParameters parameters)
            throws InvalidAlgorithmParameterException;
}
