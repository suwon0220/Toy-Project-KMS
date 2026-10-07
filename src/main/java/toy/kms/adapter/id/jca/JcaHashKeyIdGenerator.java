package toy.kms.adapter.id.jca;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import toy.kms.application.port.KeyIdGenerator;
import toy.kms.domain.KeyId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HexFormat;

@Component
@Validated
public class JcaHashKeyIdGenerator implements KeyIdGenerator {

    @Override
    public KeyId generate(@NotNull PublicKey publicKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKey.getEncoded());

            return new KeyId("SHA-256:" + HexFormat.of().formatHex(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
