package toy.kms.application.persistence;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import toy.kms.application.port.ManagedKeyIdGenerator;
import toy.kms.domain.key.ManagedKeyId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HexFormat;

@Component
@Validated
public class Sha256DigestManagedKeyIdGenerator implements ManagedKeyIdGenerator {

    @Override
    public ManagedKeyId generate(@NotNull PublicKey publicKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKey.getEncoded());

            return new ManagedKeyId("SHA-256:" + HexFormat.of().formatHex(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
