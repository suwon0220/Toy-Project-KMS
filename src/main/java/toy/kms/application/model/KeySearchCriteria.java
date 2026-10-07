package toy.kms.application.model;

import toy.kms.domain.KeyAlgorithm;
import toy.kms.domain.ManagedKey;

import java.util.Locale;
import java.util.function.Predicate;

public record KeySearchCriteria(
        KeyAlgorithm algorithm,
        String keyword) {

    public KeySearchCriteria {
        keyword = keyword == null || keyword.isBlank()
                ? null
                : keyword.strip().toLowerCase(Locale.ROOT);
    }

    public Predicate<ManagedKey> toPredicate() {
        return key -> (algorithm == null || key.getKeyAlgorithmPreset().toParameters().algorithm() == algorithm)
                && (keyword == null
                || key.getKeyId().value().toLowerCase(Locale.ROOT).contains(keyword)
                || (key.getAlias() != null && key.getAlias().toLowerCase(Locale.ROOT).contains(keyword)));
    }
}
