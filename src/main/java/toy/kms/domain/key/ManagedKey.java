package toy.kms.domain.key;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import toy.kms.application.web.KeyAlgorithmPreset;

import java.time.Instant;

@Entity
@Validated
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagedKey {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "managed_key_id"))
    private ManagedKeyId id; // Unique identifier for the key
    private Instant createdAt; // Timestamp of key creation

    @Nullable
    private String alias; // Optional alias for the Managed key

    // To cache the algorithm used for the key, we can store it as a separate field. This is useful for quick access without needing to retrieve the key material.
    // TODO: change this field to have a more structured representation of the algorithm (e.g., an enum or a separate class) to avoid dependency on the KeyAlgorithmPreset class in the web layer. This will make the domain model more self-contained and less coupled to the web layer.
    KeyAlgorithmPreset keyAlgorithmPreset; // Algorithm used for the key

    // TODO: add KeyStatus enum and use it to represent the status of the key for lifecycle management (e.g., ACTIVE, INACTIVE, DELETED)
//    KeyStatus keyStatus; // Status of the key (e.g., ACTIVE, INACTIVE, DELETED)

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(
                    name = "key_ref",
                    nullable = false,
                    updatable = false
            )
    )
    private KeyRef keyRef; // Reference to the key (Provider-specific)

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(
                    name = "provider_id",
                    nullable = false,
                    updatable = false
            )
    )
    private KeyProviderId keyProviderId;
}
