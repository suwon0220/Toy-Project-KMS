package toy.kms.domain.signature;

public record RsaPkcs1SignatureParameters(
        DigestAlgorithm hashAlgorithm
) implements SignatureParameters {
}
