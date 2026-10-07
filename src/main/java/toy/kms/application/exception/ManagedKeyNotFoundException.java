package toy.kms.application.exception;

import toy.kms.domain.KeyId;

public class ManagedKeyNotFoundException extends RuntimeException {
    public ManagedKeyNotFoundException(KeyId keyId) {
        super("ManagedKey not found: " + keyId.value());
    }
}
