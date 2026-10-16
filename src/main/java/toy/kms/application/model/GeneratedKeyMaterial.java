package toy.kms.application.model;

import toy.kms.domain.key.KeyRef;
import toy.kms.keyprovider.local.domain.ProvidedKey;

import java.security.PublicKey;

public record GeneratedKeyMaterial(
        KeyRef reference,
        PublicKey publicKey
) {
    public static GeneratedKeyMaterial from(ProvidedKey providedKey) {
        return new GeneratedKeyMaterial(providedKey.getKeyRef(), providedKey.getKeyPair().getPublic());
    }
}
