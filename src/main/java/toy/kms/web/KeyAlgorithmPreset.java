package toy.kms.web;

import toy.kms.domain.generation.*;

public enum KeyAlgorithmPreset {
    RSA_2048,
    RSA_3072,
    RSA_4096,
    EC_P256,
    EC_P384,
    EC_P521,
    ED25519,
    ED448;

    public static String displayName(KeyGenerationParameters parameters) {
        for (KeyAlgorithmPreset preset : values()) {
            if (preset.toParameters().equals(parameters)) {
                return preset.name();
            }
        }
        if (parameters instanceof RsaKeyGenerationParameters(int keySize)) {
            return "RSA_" + keySize;
        }
        if (parameters instanceof EcKeyGenerationParameters(String curve)) {
            return "EC_" + curve;
        }
        return parameters.algorithm().name();
    }

    public static KeyAlgorithmPreset fromString(String name) throws IllegalArgumentException {
        return KeyAlgorithmPreset.valueOf(name);
    }

    public KeyGenerationParameters toParameters() {
        return switch (this) {
            case RSA_2048 -> new RsaKeyGenerationParameters(2048);
            case RSA_3072 -> new RsaKeyGenerationParameters(3072);
            case RSA_4096 -> new RsaKeyGenerationParameters(4096);

            case EC_P256 -> new EcKeyGenerationParameters("secp256r1");
            case EC_P384 -> new EcKeyGenerationParameters("secp384r1");
            case EC_P521 -> new EcKeyGenerationParameters("secp521r1");

            case ED25519 -> new Ed25519KeyGenerationParameters();
            case ED448 -> new Ed448KeyGenerationParameters();
        };
    }
}
