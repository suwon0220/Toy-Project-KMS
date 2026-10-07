package toy.kms.infrastructure.jca.signer;

import org.springframework.beans.factory.annotation.Autowired;
import toy.kms.adapter.keymaterial.jca.JcaSignatureOperator;

import java.security.Provider;

public abstract class AbstractJcaSignatureOperator implements JcaSignatureOperator {
    @Autowired
    protected Provider provider;

}
