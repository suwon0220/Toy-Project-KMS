package toy.kms.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.service.KeyManagementService;
import toy.kms.domain.*;
import toy.kms.web.form.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kms/api/v1")
public class KeyAPIController {

    private static final String DEFAULT_PROVIDER_ID = "in-memory";
    private final KeyManagementService keyManagementService;

    @GetMapping("/algorithms")
    public Map<String, Object> algorithms() {
        return Map.of(
                "SignatureAlgorithms", SignatureAlgorithm.values(),
                "HashAlgorithms", DigestAlgorithm.values(),
                "KeyAlgorithms", KeyAlgorithmPreset.values()
        );
    }

    @GetMapping("/keys")
    public List<KeyDataForm> listKeys(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) KeyAlgorithm keyAlgorithm) {
        if (keyword != null || keyAlgorithm != null) {
            KeySearchCriteria criteria = new KeySearchCriteria(keyAlgorithm, keyword);
            return keyManagementService.search(criteria).stream().map(KeyDataForm::from).collect(Collectors.toList());
        }
        return keyManagementService.findAll().stream().map(KeyDataForm::from).collect(Collectors.toList());
    }

    @GetMapping("/keys/pub")
    public PubKeyResponseForm getPublicKey(@RequestBody PubKeyRequestForm pubKeyRequestForm) {
        log.debug("Received request for public key with keyId: {}", pubKeyRequestForm.id());
        return new PubKeyResponseForm(pubKeyRequestForm.id(), keyManagementService.getPublicKeyOf(pubKeyRequestForm.id()));
    }

    @PostMapping("/keys/generate")
    public KeyDataForm generateKey(@RequestBody @Validated KeyIssueForm keyIssueForm) throws Exception {
        ManagedKey managedKey = keyManagementService.generate(DEFAULT_PROVIDER_ID, keyIssueForm.keyAlgorithm(), keyIssueForm.alias());
        return KeyDataForm.from(managedKey);
    }

    @PostMapping("/keys/sign")
    public SignResponseForm signData(@RequestBody @Validated SignRequestForm signRequestForm) throws Exception {
        return new SignResponseForm(keyManagementService.sign(signRequestForm.keyId(), signRequestForm.signatureAlgorithm(), signRequestForm.digestAlgorithm(), signRequestForm.data()));
    }

    @PostMapping("/keys/verify")
    public VerifyResponseForm verifySignature(@RequestBody @Validated VerifyRequestForm verifyRequestForm) throws Exception {
        return new VerifyResponseForm(keyManagementService.verify(verifyRequestForm.keyId(), verifyRequestForm.signatureAlgorithm(), verifyRequestForm.digestAlgorithm(), verifyRequestForm.data(), verifyRequestForm.signature()));
    }

    @PostMapping("/keys/delete")
    public List<KeyId> disableKey(@RequestBody KeyId[] keyId) {
        List<KeyId> keyIds = new LinkedList<>();
        for (KeyId id : keyId) {
            keyIds.add(keyManagementService.delete(id));
        }
        return keyIds;
    }

}
