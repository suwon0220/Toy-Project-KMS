package toy.kms.application.port;

import toy.kms.application.model.KeySearchCriteria;
import toy.kms.domain.KeyId;
import toy.kms.domain.ManagedKey;

import java.util.List;
import java.util.Optional;

public interface KeyRepository {
    List<ManagedKey> findByCriteria(KeySearchCriteria criteria);

    List<ManagedKey> findAll();

    void save(ManagedKey managedKey);

    Optional<ManagedKey> findById(KeyId keyId);

    void delete(KeyId keyId);
}
