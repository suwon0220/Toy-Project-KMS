package toy.kms.application.port;

import toy.kms.domain.KeyMaterialRef;

import java.security.PublicKey;

public interface KeyMaterialRefGenerator {
    KeyMaterialRef generate(PublicKey publicKey);
}
