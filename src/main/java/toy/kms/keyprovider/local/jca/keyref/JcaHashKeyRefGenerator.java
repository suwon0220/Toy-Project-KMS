package toy.kms.keyprovider.local.jca.keyref;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.domain.key.KeyRef;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class JcaHashKeyRefGenerator implements KeyRefGenerator {

    private final MessageDigest keyIdMessageDigest;

    /**
     * KeyID 생성
     *
     * @param publicKey 공개키
     * @return KeyId "<algorithm>:<hash>"로 생성된 KeyId
     */
    @Override
    public KeyRef generateKeyRef(PublicKey publicKey) {
        byte[] hash = keyIdMessageDigest.digest(publicKey.getEncoded());
        return new KeyRef(keyIdMessageDigest.getAlgorithm() + ":" + HexFormat.of().formatHex(hash));
    }
}
