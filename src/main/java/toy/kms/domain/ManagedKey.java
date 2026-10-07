package toy.kms.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import toy.kms.domain.generation.KeyGenerationParameters;
import toy.kms.web.KeyAlgorithmPreset;

import java.time.Instant;

@Data
@Validated
@RequiredArgsConstructor
public class ManagedKey {
    public static final int MAX_ALIAS_LENGTH = 64;

    private final KeyId keyId; // Unique identifier for the key
    private final KeyAlgorithmPreset keyAlgorithmPreset; // Preset for the key algorithm
    private final KeyMaterialRef keyMaterialRef; // Reference to the key material (Provider-specific)
    private final KeyProviderId keyProviderId;
    private final Instant createdAt;
    private String alias; // Optional alias for the key

    public void setAlias(String alias) {
        String normalized = alias == null ? null : alias.strip();
        if (normalized != null && normalized.length() > MAX_ALIAS_LENGTH) {
            throw new IllegalArgumentException("Key alias must be at most " + MAX_ALIAS_LENGTH + " characters");
        }
        this.alias = normalized == null || normalized.isEmpty() ? null : normalized;
    }
}
