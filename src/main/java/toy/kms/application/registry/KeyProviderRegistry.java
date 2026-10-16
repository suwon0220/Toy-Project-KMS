package toy.kms.application.registry;

import org.springframework.stereotype.Component;
import toy.kms.application.port.KeyProviderService;
import toy.kms.domain.key.KeyProviderId;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KeyProviderRegistry {
    private final Map<KeyProviderId, KeyProviderService> providers;

    public KeyProviderRegistry(Collection<KeyProviderService> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toUnmodifiableMap(KeyProviderService::id, Function.identity()));
    }

    public KeyProviderService get(KeyProviderId keyProviderId) {
        KeyProviderService provider = providers.get(keyProviderId);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown keyProviderId: " + keyProviderId);
        }
        return provider;
    }
}
