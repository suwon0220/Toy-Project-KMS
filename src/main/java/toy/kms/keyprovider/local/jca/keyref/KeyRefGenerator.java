package toy.kms.keyprovider.local.jca.keyref;

import toy.kms.domain.key.KeyRef;

import java.security.PublicKey;

public interface KeyRefGenerator {
    KeyRef generateKeyRef(PublicKey publicKey);
}
