package toy.kms.keyprovider.local.jca.signer;

import org.springframework.beans.factory.annotation.Autowired;
import toy.kms.keyprovider.adapter.Signer;

import java.security.Provider;

public abstract class AbstractSigner implements Signer {
    @Autowired
    protected Provider provider;

}
