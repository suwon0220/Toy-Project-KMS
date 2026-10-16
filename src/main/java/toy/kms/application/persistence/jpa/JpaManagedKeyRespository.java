package toy.kms.application.persistence.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.port.ManagedKeyRepository;
import toy.kms.domain.key.ManagedKey;
import toy.kms.domain.key.ManagedKeyId;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaManagedKeyRespository implements ManagedKeyRepository {

    private final SpringDataJpaManagedKeyRepository springDataJpaManagedKeyRepository;
    private final ManagedKeyQueryRepository managedKeyQueryRepository;

    @Override
    public List<ManagedKey> findAll(KeySearchCriteria criteria) {
        return managedKeyQueryRepository.findAll(criteria);
    }

    @Override
    public void save(ManagedKey managedKey) {
        springDataJpaManagedKeyRepository.save(managedKey);
    }

    @Override
    public Optional<ManagedKey> findById(ManagedKeyId managedKeyId) {
        return springDataJpaManagedKeyRepository.findById(managedKeyId);
    }

    @Override
    public void delete(ManagedKeyId managedKeyId) {
        springDataJpaManagedKeyRepository.deleteById(managedKeyId);
    }
}
