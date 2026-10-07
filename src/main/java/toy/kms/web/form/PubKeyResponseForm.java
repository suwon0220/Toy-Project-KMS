package toy.kms.web.form;

import toy.kms.domain.KeyId;

public record PubKeyResponseForm(
        KeyId keyId,
        byte[] publicKey
) {
}
