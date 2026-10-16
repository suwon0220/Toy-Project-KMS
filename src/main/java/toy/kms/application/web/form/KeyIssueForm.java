package toy.kms.application.web.form;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

@Validated
public record KeyIssueForm(
        String alias,
        @NotEmpty
        String keyAlgorithm) {
}
