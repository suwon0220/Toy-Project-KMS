package toy.kms.domain.signature;

import toy.kms.domain.DigestAlgorithm;

public record RsaPssSignatureParameters(
        DigestAlgorithm hashAlgorithm,
        DigestAlgorithm mgf1HashAlgorithm,
        int saltLength
) implements SignatureParameters {
}
