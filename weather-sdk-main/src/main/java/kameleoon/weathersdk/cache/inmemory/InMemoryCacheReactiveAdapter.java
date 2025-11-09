package kameleoon.weathersdk.cache.inmemory;

import kameleoon.weathersdk.cache.ReactiveCacheManager;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.Set;

public class InMemoryCacheReactiveAdapter<T> implements ReactiveCacheManager<T> {

    private final InMemoryCacheManager<T> delegate;

    public InMemoryCacheReactiveAdapter(InMemoryCacheManager<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<T> get(@NonNull String key) {
        return Mono.justOrEmpty(delegate.get(key));
    }

    @Override
    public Mono<Boolean> put(@NonNull String key, @NonNull T value) {
        delegate.put(key, value);
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> remove(@NonNull String key) {
        delegate.remove(key);
        return Mono.just(true);
    }

    public Mono<Set<String>> keySet() {
        return Mono.fromCallable(delegate::keySet);
    }

    public Mono<Void> clear() {
        delegate.clear();
        return Mono.empty();
    }
}