package toy.kms.application.registry;

import org.springframework.stereotype.Component;
import toy.kms.application.port.KeyMaterialProvider;
import toy.kms.domain.KeyProviderId;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KeyMaterialProviderRegistry {
    private final Map<KeyProviderId, KeyMaterialProvider> providers;

    public KeyMaterialProviderRegistry(Collection<KeyMaterialProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toUnmodifiableMap(KeyMaterialProvider::id, Function.identity()));
    }

    public KeyMaterialProvider get(KeyProviderId keyProviderId) {
        KeyMaterialProvider provider = providers.get(keyProviderId);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown keyProviderId: " + keyProviderId);
        }
        return provider;
    }
}
