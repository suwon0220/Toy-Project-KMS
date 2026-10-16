package toy.kms.keyprovider.local.jca.persistence.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import toy.kms.domain.key.KeyRef;
import toy.kms.keyprovider.local.domain.ProvidedKey;
import toy.kms.keyprovider.local.jca.persistence.ProviderKeyRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaProviderKeyRepository implements ProviderKeyRepository {

    private final SpringDataJpaProvidedKeyRepository springDataJpaProvidedKeyRepository;

    @Override
    public List<ProvidedKey> findAll() {
        return springDataJpaProvidedKeyRepository.findAll();
    }

    @Override
    public void save(ProvidedKey providedKey) {
        springDataJpaProvidedKeyRepository.save(providedKey);
    }

    @Override
    public Optional<ProvidedKey> findById(KeyRef keyRef) {
        return springDataJpaProvidedKeyRepository.findById(keyRef);
    }

    @Override
    public void delete(KeyRef keyRef) {
        springDataJpaProvidedKeyRepository.findById(keyRef).
            ifPresent(providedKey -> {
                log.info("Deleting ProvidedKey with keyRef: {}", keyRef);
                springDataJpaProvidedKeyRepository.delete(providedKey);
            });
    }
}
