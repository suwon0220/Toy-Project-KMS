package toy.kms.application.model;

import toy.kms.domain.key.KeyAlgorithm;
import toy.kms.domain.key.ManagedKey;

import java.util.Locale;
import java.util.function.Predicate;

public record KeySearchCriteria(
        KeyAlgorithm algorithm,
        String keyword) {

}
