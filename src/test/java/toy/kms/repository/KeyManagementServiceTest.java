package toy.kms.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import toy.kms.application.model.KeySearchCriteria;
import toy.kms.application.registry.KeyMaterialProviderRegistry;
import toy.kms.application.service.KeyManagementService;
import toy.kms.domain.DigestAlgorithm;
import toy.kms.domain.ManagedKey;
import toy.kms.domain.SignatureAlgorithm;
import toy.kms.web.KeyAlgorithmPreset;

import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class KeyManagementServiceTest {

    private static final String PROVIDER_ID = "in-memory";

    @Autowired private KeyManagementService keyManagementService;
    @Autowired private KeyMaterialProviderRegistry keyMaterialProviderRegistry;
    @Autowired private Provider cryptographicProvider;

    private ManagedKey managedKey;

    @AfterEach
    void deleteGeneratedKey() {
        if (managedKey != null) {
            keyManagementService.delete(managedKey.getKeyId());
        }
    }

    static Stream<Arguments> signatureCases() {
        return Stream.of(
            Arguments.of(KeyAlgorithmPreset.RSA_2048, SignatureAlgorithm.RSA_PKCS1_V1_5,
                DigestAlgorithm.SHA256, "SHA256withRSA"),
            Arguments.of(KeyAlgorithmPreset.RSA_2048, SignatureAlgorithm.RSA_PKCS1_V1_5,
                DigestAlgorithm.SHA384, "SHA384withRSA"),
            Arguments.of(KeyAlgorithmPreset.RSA_2048, SignatureAlgorithm.RSA_PKCS1_V1_5,
                DigestAlgorithm.SHA512, "SHA512withRSA"),
            Arguments.of(KeyAlgorithmPreset.EC_P256, SignatureAlgorithm.ECDSA,
                DigestAlgorithm.SHA256, "SHA256withECDSA"),
            Arguments.of(KeyAlgorithmPreset.EC_P256, SignatureAlgorithm.ECDSA,
                DigestAlgorithm.SHA384, "SHA384withECDSA"),
            Arguments.of(KeyAlgorithmPreset.EC_P256, SignatureAlgorithm.ECDSA,
                DigestAlgorithm.SHA512, "SHA512withECDSA"),
            Arguments.of(KeyAlgorithmPreset.EC_P521, SignatureAlgorithm.ECDSA,
                DigestAlgorithm.SHA256, "SHA256withECDSA"),
            Arguments.of(KeyAlgorithmPreset.EC_P521, SignatureAlgorithm.ECDSA,
                DigestAlgorithm.SHA384, "SHA384withECDSA"),
            Arguments.of(KeyAlgorithmPreset.EC_P521, SignatureAlgorithm.ECDSA,
                DigestAlgorithm.SHA512, "SHA512withECDSA"),
            Arguments.of(KeyAlgorithmPreset.ED25519, SignatureAlgorithm.Ed25519,
                null, "Ed25519"),
            Arguments.of(KeyAlgorithmPreset.ED448, SignatureAlgorithm.Ed448,
                null, "Ed448"));
    }

    @ParameterizedTest(name = "[{index}] in-memory / {0}")
    @EnumSource(KeyAlgorithmPreset.class)
    void testInMemoryGenerate(KeyAlgorithmPreset preset) throws Exception {
        // When
        managedKey = keyManagementService.generate(PROVIDER_ID, preset.name());

        // Then
        assertThat(managedKey).isNotNull();
        assertThat(managedKey.getKeyId()).isNotNull();
        assertThat(managedKey.getKeyAlgorithmPreset()).isEqualTo(preset);
        assertThat(managedKey.getKeyMaterialRef()).isNotNull();
        assertThat(managedKey.getKeyProviderId().value()).isEqualTo(PROVIDER_ID);
        assertThat(managedKey.getCreatedAt()).isNotNull();
        assertThat(keyManagementService.findById(managedKey.getKeyId())).contains(managedKey);
        assertThat(keyManagementService.search(new KeySearchCriteria(
            preset.toParameters().algorithm(), managedKey.getKeyId().value())))
            .containsExactly(managedKey);
    }

    @ParameterizedTest(name = "[{index}] in-memory / {0} / {3}")
    @MethodSource("signatureCases")
    void testInMemorySign(
        KeyAlgorithmPreset preset,
        SignatureAlgorithm signatureAlgorithm,
        DigestAlgorithm digestAlgorithm,
        String jcaAlgorithm) throws Exception {
        // Given
        managedKey = keyManagementService.generate(PROVIDER_ID, preset.name());
        byte[] data = "Hello World!\n".getBytes(StandardCharsets.UTF_8);
        byte[] tamperedData = "tampered".getBytes(StandardCharsets.UTF_8);

        PublicKey publicKey = keyMaterialProviderRegistry
            .get(managedKey.getKeyProviderId())
            .getPublicKey(managedKey.getKeyMaterialRef())
            .orElseThrow();

        // When
        byte[] signed = keyManagementService.sign(
            managedKey.getKeyId(), signatureAlgorithm, digestAlgorithm, data);

        // Then: 원본 데이터 검증 성공
        assertThat(signed).isNotEmpty();
        Signature verifier = Signature.getInstance(jcaAlgorithm, cryptographicProvider);
        verifier.initVerify(publicKey);
        verifier.update(data);
        assertThat(verifier.verify(signed)).isTrue();
        assertThat(keyManagementService.verify(
            managedKey.getKeyId(), signatureAlgorithm, digestAlgorithm, data, signed)).isTrue();

        // 변조된 데이터 검증 실패
        verifier.initVerify(publicKey);
        verifier.update(tamperedData);
        assertThat(verifier.verify(signed)).isFalse();
        assertThat(keyManagementService.verify(
            managedKey.getKeyId(), signatureAlgorithm, digestAlgorithm, tamperedData, signed)).isFalse();
    }

    @ParameterizedTest(name = "[{index}] in-memory / RSA_PSS / {0}")
    @EnumSource(DigestAlgorithm.class)
    void testInMemoryRsaPssIsNotSupported(DigestAlgorithm digestAlgorithm) throws Exception {
        managedKey = keyManagementService.generate(PROVIDER_ID, KeyAlgorithmPreset.RSA_2048.name());
        byte[] data = "Hello World!\n".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> keyManagementService.sign(
            managedKey.getKeyId(), SignatureAlgorithm.RSA_PSS, digestAlgorithm, data))
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("RSA_PSS is not supported yet");
        assertThatThrownBy(() -> keyManagementService.verify(
            managedKey.getKeyId(), SignatureAlgorithm.RSA_PSS, digestAlgorithm, data, new byte[0]))
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("RSA_PSS is not supported yet");
    }
}
