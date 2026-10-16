package toy.kms.application.port;

import jakarta.validation.constraints.NotNull;
import toy.kms.domain.key.ManagedKeyId;

import java.security.PublicKey;

public interface ManagedKeyIdGenerator {
    ManagedKeyId generate(@NotNull PublicKey publicKey);
}
