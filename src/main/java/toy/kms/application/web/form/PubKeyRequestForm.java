package toy.kms.application.web.form;

import jakarta.validation.constraints.NotEmpty;
import toy.kms.domain.key.ManagedKeyId;

public record PubKeyRequestForm(
        @NotEmpty ManagedKeyId id
) {
}
