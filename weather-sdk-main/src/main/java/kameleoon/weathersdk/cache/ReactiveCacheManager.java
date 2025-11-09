package kameleoon.weathersdk.cache;

import reactor.core.publisher.Mono;

import java.util.Set;

public interface ReactiveCacheManager<T> {
    Mono<T> get(final String key);

    Mono<Boolean> put(final String key, final T value);

    Mono<Boolean> remove(final String key);

    Mono<Set<String>> keySet();

    Mono<Void> clear();
}