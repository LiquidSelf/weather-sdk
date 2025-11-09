package kameleoon.weathersdk.cache;

import lombok.NonNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

//add async methods?
public interface CacheManager<T> {

    T get(final String key);

    CompletableFuture<T> getAsync(@NonNull final String key);

    T fetchIfAbsent(@NonNull final String key, @NonNull Function<String, T> fetchFunction);

    void put(final String key, final T value);

    void remove(final String key);

    Set<String> keySet();

    void clear();
}
