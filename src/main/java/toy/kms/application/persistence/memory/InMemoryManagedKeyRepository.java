package toy.kms.application.persistence.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.port.ManagedKeyRepository;
import toy.kms.domain.key.ManagedKeyId;
import toy.kms.domain.key.ManagedKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemoryManagedKeyRepository implements ManagedKeyRepository {

    private final Map<ManagedKeyId, ManagedKey> store = new ConcurrentHashMap<>();

    @Override
    public List<ManagedKey> findAll(KeySearchCriteria criteria) {
        throw new UnsupportedOperationException("findAll is not supported in InMemoryManagedKeyRepository");
    }

    @Override
    public void save(ManagedKey managedKey) {
        if (store.putIfAbsent(managedKey.getId(), managedKey) != null) {
            throw new IllegalStateException("Key with id " + managedKey.getId() + " already exists");
        }
    }

    @Override
    public Optional<ManagedKey> findById(ManagedKeyId managedKeyId) {
        return Optional.ofNullable(store.get(managedKeyId));
    }

    @Override
    public void delete(ManagedKeyId managedKeyId) {
        store.remove(managedKeyId);
    }
}
