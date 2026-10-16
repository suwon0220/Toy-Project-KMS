package toy.kms.application.port;

import toy.kms.application.model.KeySearchCriteria;
import toy.kms.domain.key.ManagedKeyId;
import toy.kms.domain.key.ManagedKey;

import java.util.List;
import java.util.Optional;

public interface ManagedKeyRepository {
    List<ManagedKey> findAll(KeySearchCriteria criteria);

    void save(ManagedKey managedKey);

    Optional<ManagedKey> findById(ManagedKeyId managedKeyId);

    void delete(ManagedKeyId managedKeyId);
}
