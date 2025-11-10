package kameleoon.weathersdk.cache;

import lombok.NonNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Simple cache manager for synchronous and async access.
 */
//add async methods?
public interface CacheManager<T> {

    /**
     * Returns cached value or null if absent.
     */
    T get(final String key);

    /**
     * Returns cached value asynchronously or completes with null if absent.
     */
    CompletableFuture<T> getAsync(@NonNull final String key);

    /**
     * Returns cached value or fetches and stores it if missing.
     */
    T fetchIfAbsent(@NonNull final String key, @NonNull Function<String, T> fetchFunction);

    /**
     * Puts value into cache.
     */
    void put(final String key, final T value);

    /**
     * Removes value from cache.
     */
    void remove(final String key);

    /**
     * Returns all cache keys.
     */
    Set<String> keySet();

    /**
     * Clears all cached entries.
     */
    void clear();
}
