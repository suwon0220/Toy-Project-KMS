package toy.kms.application.web.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import toy.kms.domain.signature.DigestAlgorithm;
import toy.kms.domain.key.ManagedKeyId;
import toy.kms.domain.signature.SignatureAlgorithm;

public record SignRequestForm(
        @NotNull @Valid @JsonAlias("keyId")
        ManagedKeyId managedKeyId,
        SignatureAlgorithm signatureAlgorithm,
        DigestAlgorithm digestAlgorithm,
        byte[] data
) {
}
