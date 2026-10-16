package toy.kms.domain.key;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

@Embeddable
public record KeyProviderId(@NotEmpty String value) {
}
