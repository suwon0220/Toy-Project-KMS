package toy.kms.domain.generation;

import toy.kms.domain.key.KeyAlgorithm;

public record RsaKeyGenerationParameters(
        int keySize
) implements KeyGenerationParameters {

    @Override
    public KeyAlgorithm algorithm() {
        return KeyAlgorithm.RSA;
    }
}
