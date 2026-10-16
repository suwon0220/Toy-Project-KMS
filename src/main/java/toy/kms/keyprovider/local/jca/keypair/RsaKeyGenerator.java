package toy.kms.keyprovider.local.jca.keypair;

import org.springframework.stereotype.Component;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.generation.RsaKeyGenerationParameters;

import java.security.KeyPairGenerator;

@Component
public class RsaKeyGenerator extends AbstractKeyGenerator {

    @Override
    public boolean supports(KeyGenerationParameters parameters) {
        return parameters instanceof RsaKeyGenerationParameters;
    }

    @Override
    protected void initializeKeyPairGenerator(KeyPairGenerator keyPairGenerator, KeyGenerationParameters parameters) {
        RsaKeyGenerationParameters keyGenerationParameter = (RsaKeyGenerationParameters) parameters;
        keyPairGenerator.initialize(keyGenerationParameter.keySize());
    }


}
