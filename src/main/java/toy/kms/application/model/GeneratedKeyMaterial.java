package toy.kms.application.model;

import toy.kms.domain.KeyMaterialRef;

import java.security.PublicKey;

public record GeneratedKeyMaterial(
        KeyMaterialRef reference,
        PublicKey publicKey
) {
}
