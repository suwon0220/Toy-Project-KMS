package toy.kms.adapter.persistence.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.port.KeyRepository;
import toy.kms.domain.KeyId;
import toy.kms.domain.ManagedKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryManagedKeyRepository implements KeyRepository {

    private final Map<KeyId, ManagedKey> store = new ConcurrentHashMap<>();

    @Override
    public List<ManagedKey> findByCriteria(KeySearchCriteria criteria) {
        return store.values()
                .stream()
                .filter(criteria.toPredicate())
                .toList();
    }

    @Override
    public List<ManagedKey> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void save(ManagedKey managedKey) {
        if (store.putIfAbsent(managedKey.getKeyId(), managedKey) != null) {
            throw new IllegalStateException("Key with id " + managedKey.getKeyId() + " already exists");
        }
    }

    @Override
    public Optional<ManagedKey> findById(KeyId keyId) {
        return Optional.ofNullable(store.get(keyId));
    }

    @Override
    public void delete(KeyId keyId) {
        store.remove(keyId);
    }
}
