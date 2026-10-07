package toy.kms.web.form;

import lombok.Data;
import toy.kms.domain.KeyId;
import toy.kms.domain.ManagedKey;
import toy.kms.web.KeyAlgorithmPreset;

import java.time.Instant;

@Data
public class KeyDataForm {
    private final KeyId id;
    private final String alias;
    private final KeyAlgorithmPreset keyAlgorithm;
    private final Instant createdAt;

    public static KeyDataForm from(ManagedKey managedKey) {
        return new KeyDataForm(
                managedKey.getKeyId(),
                managedKey.getAlias(),
                managedKey.getKeyAlgorithmPreset(),
                managedKey.getCreatedAt()
        );
    }
}
