package toy.kms.infrastructure.jca.signer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.adapter.keymaterial.jca.JcaSignatureOperator;
import toy.kms.domain.signature.SignatureParameters;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JcaSignatureOperatorRegistry {

    private final List<JcaSignatureOperator> signers;

    public JcaSignatureOperator get(SignatureParameters parameters) {
        List<JcaSignatureOperator> matches = signers.stream()
                .filter(signer -> signer.supports(parameters))
                .toList();

        if (matches.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unsupported signature parameters: " + parameters);
        }

        if (matches.size() > 1) {
            throw new IllegalStateException(
                    "Multiple signers support parameters: " + parameters);
        }

        return matches.get(0);
    }

}
