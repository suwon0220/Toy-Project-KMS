package toy.kms.keyprovider.local.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import toy.kms.domain.key.KeyRef;

import java.security.KeyPair;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvidedKey {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "key_ref"))
    private KeyRef keyRef; // Reference to the key (Provider-specific)

    private String algorithm; // Algorithm used for the key (e.g., RSA 2048, RSA 4096, EC P-256, etc.)

    @Lob
    private KeyPair keyPair; // The actual key pair (public/private keys)
}
