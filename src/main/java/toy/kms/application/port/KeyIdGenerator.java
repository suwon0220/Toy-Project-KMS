package toy.kms.application.port;

import jakarta.validation.constraints.NotNull;
import toy.kms.domain.KeyId;

import java.security.PublicKey;

public interface KeyIdGenerator {
    KeyId generate(@NotNull PublicKey publicKey);
}
