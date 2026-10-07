package toy.kms.infrastructure.jca.keygenerator;

import org.springframework.stereotype.Component;
import toy.kms.domain.generation.EcKeyGenerationParameters;
import toy.kms.domain.generation.KeyGenerationParameters;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;

@Component
public class EcJcaKeyGenerator extends AbstractJcaKeyGenerator {

    @Override
    public boolean supports(KeyGenerationParameters parameters) {
        return parameters instanceof EcKeyGenerationParameters;
    }

    @Override
    protected void initializeKeyPairGenerator(KeyPairGenerator keyPairGenerator, KeyGenerationParameters parameters)
            throws InvalidAlgorithmParameterException {
        EcKeyGenerationParameters keyGenerationParameter = (EcKeyGenerationParameters) parameters;
        keyPairGenerator.initialize(new ECGenParameterSpec(keyGenerationParameter.curve()));
    }


}
