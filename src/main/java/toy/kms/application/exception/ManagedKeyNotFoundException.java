package toy.kms.application.exception;

import toy.kms.domain.key.ManagedKeyId;

public class ManagedKeyNotFoundException extends RuntimeException {
    public ManagedKeyNotFoundException(ManagedKeyId managedKeyId) {
        super("ManagedKey not found: " + managedKeyId.value());
    }
}
