package toy.kms.application.persistence.jpa;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.web.KeyAlgorithmPreset;
import toy.kms.domain.key.KeyAlgorithm;
import toy.kms.domain.key.ManagedKey;

import java.util.Arrays;
import java.util.List;

import static toy.kms.domain.key.QManagedKey.managedKey;

@Repository
@Transactional(readOnly = true)
public class ManagedKeyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ManagedKeyQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<ManagedKey> findAll(KeySearchCriteria criteria) {
        return queryFactory.selectFrom(managedKey)
                .where(
                        algorithmMatches(criteria == null ? null : criteria.algorithm()),
                        keywordContains(criteria == null ? null : criteria.keyword()))
                .fetch();
    }

    private BooleanExpression algorithmMatches(KeyAlgorithm algorithm) {
        return algorithm != null
                ? managedKey.keyAlgorithmPreset.in(Arrays.stream(KeyAlgorithmPreset.values())
                        .filter(preset -> preset.toParameters().algorithm() == algorithm)
                        .toList())
                : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword)
                ? managedKey.id.value.containsIgnoreCase(keyword)
                    .or(managedKey.alias.containsIgnoreCase(keyword))
                : null;

    }
}
