package toy.kms.keyprovider.local.jca.keypair;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.domain.generation.Ed448KeyGenerationParameters;
import toy.kms.domain.generation.KeyGenerationParameters;

import java.security.KeyPairGenerator;

@Component
@RequiredArgsConstructor
public class Ed448KeyGenerator extends AbstractKeyGenerator {

    @Override
    public boolean supports(KeyGenerationParameters parameters) {
        return parameters instanceof Ed448KeyGenerationParameters;
    }

    @Override
    protected void initializeKeyPairGenerator(KeyPairGenerator keyPairGenerator, KeyGenerationParameters parameters) {
        // Ed448 does not require any specific initialization parameters
    }
}
