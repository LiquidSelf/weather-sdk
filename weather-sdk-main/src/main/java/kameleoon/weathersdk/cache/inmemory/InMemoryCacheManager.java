package kameleoon.weathersdk.cache.inmemory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import kameleoon.weathersdk.cache.CacheManager;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class InMemoryCacheManager<T> implements CacheManager<T> {

    private final Cache<String, T> cache;

    public InMemoryCacheManager(@NonNull InMemoryCacheConfig config) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(config.getMaxCacheRecords())
                .expireAfterWrite(config.getCacheRecordTTL())
                .build();
    }

    @Nullable
    public CompletableFuture<T> getAsync(@NonNull final String key) {
        return CompletableFuture.completedFuture(cache.getIfPresent(key));
    }

    @Nullable
    public T get(@NonNull final String key) {
        return cache.getIfPresent(key);
    }

    public T fetchIfAbsent(@NonNull final String key, @NonNull Function<String, T> fetchFunction) {
        return cache.get(key, fetchFunction);
    }

    public void put(@NonNull final String key, @NonNull final T value) {
        cache.put(key, value);
    }

    @Override
    public void remove(String key) {
        cache.invalidate(key);
    }

    public Set<String> keySet() {
        return cache.asMap().keySet();
    }

    public void clear() {
        cache.asMap().clear();
    }
}
