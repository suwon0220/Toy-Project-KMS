package toy.kms.keyprovider.local.jca.persistence;

import jakarta.transaction.Transactional;
import toy.kms.domain.key.KeyRef;
import toy.kms.keyprovider.local.domain.ProvidedKey;

import java.util.List;
import java.util.Optional;

public interface ProviderKeyRepository {
    List<ProvidedKey> findAll();

    @Transactional
    void save(ProvidedKey providedKey);

    Optional<ProvidedKey> findById(KeyRef keyRef);

    @Transactional
    void delete(KeyRef keyRef);
}
