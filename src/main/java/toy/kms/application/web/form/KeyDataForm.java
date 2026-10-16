package toy.kms.application.web.form;

import toy.kms.domain.key.ManagedKeyId;
import toy.kms.domain.key.ManagedKey;
import toy.kms.application.web.KeyAlgorithmPreset;

import java.time.Instant;

public record KeyDataForm(ManagedKeyId id, String alias, KeyAlgorithmPreset keyAlgorithm, Instant createdAt) {
    public static KeyDataForm from(ManagedKey managedKey) {
        return new KeyDataForm(
                managedKey.getId(),
                managedKey.getAlias(),
                managedKey.getKeyAlgorithmPreset(),
                managedKey.getCreatedAt()
        );
    }
}
