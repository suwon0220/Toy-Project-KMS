package toy.kms.domain.generation;

import toy.kms.domain.KeyAlgorithm;

public record Ed25519KeyGenerationParameters() implements KeyGenerationParameters {
    @Override
    public KeyAlgorithm algorithm() {
        return KeyAlgorithm.Ed25519;
    }
}
