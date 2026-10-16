package toy.kms.domain.key;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Embeddable
public record ManagedKeyId(@NotBlank String value) {
}
