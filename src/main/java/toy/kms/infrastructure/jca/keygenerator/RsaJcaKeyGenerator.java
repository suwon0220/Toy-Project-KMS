package toy.kms.infrastructure.jca.keygenerator;

import org.springframework.stereotype.Component;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.domain.generation.RsaKeyGenerationParameters;

import java.security.KeyPairGenerator;

@Component
public class RsaJcaKeyGenerator extends AbstractJcaKeyGenerator {

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
