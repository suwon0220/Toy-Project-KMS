package toy.kms.web.form;

import toy.kms.domain.KeyId;
import toy.kms.domain.ManagedKey;
import toy.kms.web.KeyAlgorithmPreset;

import java.time.Instant;

public record KeyDataForm(KeyId id, String alias, KeyAlgorithmPreset keyAlgorithm, Instant createdAt) {
    public static KeyDataForm from(ManagedKey managedKey) {
        return new KeyDataForm(
                managedKey.getKeyId(),
                managedKey.getAlias(),
                managedKey.getKeyAlgorithmPreset(),
                managedKey.getCreatedAt()
        );
    }
}
