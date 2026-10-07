package toy.kms.domain.signature;

import toy.kms.domain.DigestAlgorithm;

public record EcdsaSignatureParameters(
        DigestAlgorithm hashAlgorithm) implements SignatureParameters {

}
