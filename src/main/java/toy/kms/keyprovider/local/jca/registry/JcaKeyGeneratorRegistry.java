package toy.kms.keyprovider.local.jca.registry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.keyprovider.adapter.KeyGenerator;
import toy.kms.domain.generation.KeyGenerationParameters;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JcaKeyGeneratorRegistry {

    private final List<KeyGenerator> generators;

    public KeyGenerator get(KeyGenerationParameters parameters) {
        return generators.stream()
                .filter(generator -> generator.supports(parameters))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No JcaKeyGenerator found for parameters: " + parameters));
    }
}
