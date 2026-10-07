package toy.kms.web.form;

import jakarta.validation.constraints.NotEmpty;
import toy.kms.domain.KeyId;

public record PubKeyRequestForm(
        @NotEmpty KeyId id
) {
}
