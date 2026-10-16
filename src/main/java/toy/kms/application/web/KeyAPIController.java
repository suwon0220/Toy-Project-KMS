package toy.kms.application.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.service.KeyManagementService;
import toy.kms.application.web.form.*;
import toy.kms.domain.key.ManagedKey;
import toy.kms.domain.key.ManagedKeyId;
import toy.kms.domain.signature.DigestAlgorithm;
import toy.kms.domain.signature.SignatureAlgorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kms/api/v1/keys")
public class KeyAPIController {

    private static final String DEFAULT_PROVIDER_ID = "LocalKeyProvider";
    private final KeyManagementService keyManagementService;

    @GetMapping("/algorithms")
    public Map<String, Object> algorithms() {
        return Map.of(
                "SignatureAlgorithms", SignatureAlgorithm.values(),
                "HashAlgorithms", DigestAlgorithm.values(),
                "KeyAlgorithms", KeyAlgorithmPreset.values()
        );
    }

    @GetMapping
    public List<KeyDataForm> listKeys(
            @RequestParam(required = false) KeySearchCriteria criteria) {
        return keyManagementService.findAll(criteria).stream().map(KeyDataForm::from).collect(Collectors.toList());
    }

    @GetMapping("/pub")
    public PubKeyResponseForm getPublicKey(@RequestBody PubKeyRequestForm pubKeyRequestForm) {
        log.debug("Received request for public key with keyId: {}", pubKeyRequestForm.id());
        return new PubKeyResponseForm(pubKeyRequestForm.id(), keyManagementService.getPublicKeyOf(pubKeyRequestForm.id()));
    }

    @PostMapping("/generate")
    public KeyDataForm generateKey(@RequestBody @Validated KeyIssueForm keyIssueForm) throws Exception {
        ManagedKey managedKey = keyManagementService.generate(DEFAULT_PROVIDER_ID, keyIssueForm.keyAlgorithm(), keyIssueForm.alias());
        return KeyDataForm.from(managedKey);
    }

    @PostMapping("/sign")
    public SignResponseForm signData(@RequestBody @Validated SignRequestForm signRequestForm) {
        log.info("Received request to sign data with keyId: {}", signRequestForm.managedKeyId());
        return new SignResponseForm(keyManagementService.sign(signRequestForm.managedKeyId(), signRequestForm.signatureAlgorithm(), signRequestForm.digestAlgorithm(), signRequestForm.data()));
    }

    @PostMapping("/verify")
    public VerifyResponseForm verifySignature(@RequestBody @Validated VerifyRequestForm verifyRequestForm) {
        return new VerifyResponseForm(keyManagementService.verify(verifyRequestForm.managedKeyId(), verifyRequestForm.signatureAlgorithm(), verifyRequestForm.digestAlgorithm(), verifyRequestForm.data(), verifyRequestForm.signature()));
    }

    @PostMapping("/delete")
    public List<ManagedKeyId> disableKey(@RequestBody ManagedKeyId[] managedKeyId) {
        List<ManagedKeyId> managedKeyIds = new LinkedList<>();
        for (ManagedKeyId id : managedKeyId) {
            managedKeyIds.add(keyManagementService.delete(id));
        }
        return managedKeyIds;
    }

}
