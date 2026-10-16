package toy.kms.domain.generation;

import toy.kms.domain.key.KeyAlgorithm;

public record Ed448KeyGenerationParameters() implements KeyGenerationParameters {
    @Override
    public KeyAlgorithm algorithm() {
        return KeyAlgorithm.Ed448;
    }
}
