package toy.kms.infrastructure.jca.signer;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import toy.kms.domain.signature.Ed448SignatureParameters;
import toy.kms.domain.signature.SignatureParameters;

import java.security.*;

@Component
public class Ed448SignatureOperator extends AbstractJcaSignatureOperator {
    private static final String SIGNATURE_ALGORITHM = "Ed448";

    @Override
    public boolean supports(SignatureParameters parameters) {
        return parameters instanceof Ed448SignatureParameters;
    }

    @Override
    public byte[] sign(
            PrivateKey privateKey,
            @Nonnull SignatureParameters parameters,
            @Nonnull byte[] data) throws GeneralSecurityException {
        if (!(parameters instanceof Ed448SignatureParameters)) {
            throw new InvalidAlgorithmParameterException(
                    "Ed448 parameters are required");
        }
        Signature signature = Signature.getInstance(
                SIGNATURE_ALGORITHM,
                provider);

        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    @Override
    public boolean verify(
            @Nonnull PublicKey publicKey,
            @Nonnull SignatureParameters parameters,
            @Nonnull byte[] data,
            @Nonnull byte[] signature) throws GeneralSecurityException {
        if (!(parameters instanceof Ed448SignatureParameters)) {
            throw new InvalidAlgorithmParameterException(
                    "Ed448 parameters are required");
        }

        Signature verifier = Signature.getInstance(
                SIGNATURE_ALGORITHM,
                provider);

        verifier.initVerify(publicKey);
        verifier.update(data);
        return verifier.verify(signature);
    }
}
