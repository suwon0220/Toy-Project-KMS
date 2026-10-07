package toy.kms.infrastructure.jca;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.application.port.KeyMaterialRefGenerator;
import toy.kms.domain.KeyMaterialRef;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class JcaHashKeyMaterialRefGenerator implements KeyMaterialRefGenerator {

    private final MessageDigest keyIdMessageDigest;

    /**
     * KeyID 생성
     *
     * @param publicKey 공개키
     * @return KeyId "<algorithm>:<hash>"로 생성된 KeyId
     */
    @Override
    public KeyMaterialRef generate(PublicKey publicKey) {
        byte[] hash = keyIdMessageDigest.digest(publicKey.getEncoded());
        return new KeyMaterialRef(keyIdMessageDigest.getAlgorithm() + ":" + HexFormat.of().formatHex(hash));
    }
}
