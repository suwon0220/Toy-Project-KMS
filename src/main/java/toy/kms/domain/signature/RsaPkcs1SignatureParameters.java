package toy.kms.domain.signature;

import toy.kms.domain.DigestAlgorithm;

public record RsaPkcs1SignatureParameters(
        DigestAlgorithm hashAlgorithm
) implements SignatureParameters {
}
