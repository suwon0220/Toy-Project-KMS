package toy.kms.domain.signature;

public record EcdsaSignatureParameters(
        DigestAlgorithm hashAlgorithm) implements SignatureParameters {

}
