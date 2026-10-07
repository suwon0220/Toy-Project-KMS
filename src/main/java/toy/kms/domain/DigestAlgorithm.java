package toy.kms.domain;

public enum DigestAlgorithm {
    SHA256,
    SHA384,
    SHA512;

    public String getAlgorithmName() {
        return switch (this) {
            case SHA256 -> "SHA-256";
            case SHA384 -> "SHA-384";
            case SHA512 -> "SHA-512";
        };
    }
}
