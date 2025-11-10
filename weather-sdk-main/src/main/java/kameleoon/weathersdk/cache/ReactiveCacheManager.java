package kameleoon.weathersdk.cache;

import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Reactive cache manager based on Reactor types.
 */
public interface ReactiveCacheManager<T> {

    /**
     * Gets cached value or empty Mono if absent.
     */
    Mono<T> get(final String key);

    /**
     * Puts value into cache.
     */
    Mono<Boolean> put(final String key, final T value);

    /**
     * Removes value from cache.
     */
    Mono<Boolean> remove(final String key);

    /**
     * Returns all cache keys.
     */
    Mono<Set<String>> keySet();

    /**
     * Clears all cached entries.
     */
    Mono<Void> clear();
}