package toy.kms.web.form;

import toy.kms.domain.DigestAlgorithm;
import toy.kms.domain.KeyId;
import toy.kms.domain.SignatureAlgorithm;

public record VerifyRequestForm(
        KeyId keyId,
        SignatureAlgorithm signatureAlgorithm,
        DigestAlgorithm digestAlgorithm,
        byte[] data,
        byte[] signature) {
}
