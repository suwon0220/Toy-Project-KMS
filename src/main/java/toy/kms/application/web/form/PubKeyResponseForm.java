package toy.kms.application.web.form;

import toy.kms.domain.key.ManagedKeyId;

public record PubKeyResponseForm(
        ManagedKeyId managedKeyId,
        byte[] publicKey
) {
}
