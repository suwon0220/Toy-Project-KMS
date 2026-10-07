package toy.kms.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KeyAlgorithm {
    RSA,
    EC,
    Ed448,
    Ed25519;

    public static KeyAlgorithm fromJcaName(String jcaName) throws IllegalArgumentException {
        return KeyAlgorithm.valueOf(jcaName);
    }

    public String getJcaName() {
        return name();
    }
}
