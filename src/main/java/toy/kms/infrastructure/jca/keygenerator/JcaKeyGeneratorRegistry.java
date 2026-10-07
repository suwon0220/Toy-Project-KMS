package toy.kms.infrastructure.jca.keygenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.adapter.keymaterial.jca.JcaKeyGenerator;
import toy.kms.domain.generation.KeyGenerationParameters;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JcaKeyGeneratorRegistry {

    private final List<JcaKeyGenerator> generators;

    public JcaKeyGenerator get(KeyGenerationParameters parameters) {
        return generators.stream()
                .filter(generator -> generator.supports(parameters))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No JcaKeyGenerator found for parameters: " + parameters));
    }
}
