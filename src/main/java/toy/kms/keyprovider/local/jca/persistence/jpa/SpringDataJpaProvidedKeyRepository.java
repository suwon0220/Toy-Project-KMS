package toy.kms.keyprovider.local.jca.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.kms.domain.key.KeyRef;
import toy.kms.keyprovider.local.domain.ProvidedKey;

public interface SpringDataJpaProvidedKeyRepository extends JpaRepository<ProvidedKey, KeyRef> {
}
