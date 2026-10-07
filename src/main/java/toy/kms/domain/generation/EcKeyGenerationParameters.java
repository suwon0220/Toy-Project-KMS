package toy.kms.domain.generation;

import toy.kms.domain.KeyAlgorithm;

public record EcKeyGenerationParameters(
        String curve
) implements KeyGenerationParameters {

    @Override
    public KeyAlgorithm algorithm() {
        return KeyAlgorithm.EC;
    }
}
