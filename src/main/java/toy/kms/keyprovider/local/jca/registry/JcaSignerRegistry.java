package toy.kms.keyprovider.local.jca.registry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.kms.keyprovider.adapter.Signer;
import toy.kms.domain.signature.SignatureParameters;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JcaSignerRegistry {

    private final List<Signer> signers;

    public Signer get(SignatureParameters parameters) {
        List<Signer> matches = signers.stream()
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

        return matches.getFirst();
    }

}
