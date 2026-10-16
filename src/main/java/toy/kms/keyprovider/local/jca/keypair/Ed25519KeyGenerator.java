package toy.kms.keyprovider.local.jca.keypair;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.domain.generation.Ed25519KeyGenerationParameters;
import toy.kms.domain.generation.KeyGenerationParameters;

import java.security.KeyPairGenerator;

@Component
@RequiredArgsConstructor
public class Ed25519KeyGenerator extends AbstractKeyGenerator {

    @Override
    public boolean supports(KeyGenerationParameters parameters) {
        return parameters instanceof Ed25519KeyGenerationParameters;
    }

    @Override
    protected void initializeKeyPairGenerator(KeyPairGenerator keyPairGenerator, KeyGenerationParameters parameters) {
        // Ed25519 does not require any specific initialization parameters
    }
}
