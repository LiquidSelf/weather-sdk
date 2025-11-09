package kameleoon.weathersdk.internal.builder.standard;

import kameleoon.weathersdk.cache.CacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheConfig;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryPreconfiguredCacheHolder;
import kameleoon.weathersdk.internal.builder.WebClientBuilderBase;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.function.UnaryOperator;

@NoArgsConstructor
@AllArgsConstructor
public abstract class WebClientBuilderBaseWithCache<SELF, CACHE_TYPE, CLIENT_TYPE extends WebApiClient>
        extends WebClientBuilderBase<SELF, CLIENT_TYPE> {

    protected CacheManager<CACHE_TYPE> cacheManager;

    /**
     * @see InMemoryPreconfiguredCacheHolder
     */
    public SELF cache(@NonNull CacheManager<CACHE_TYPE> cacheManager) {
        this.cacheManager = cacheManager;
        return self();
    }

    /**
     * Creates new cache instance for each client built,
     * use {@link WebClientBuilderBaseWithCache#cache(CacheManager)} if you want shared cache instance.
     */
    public SELF inMemoryCache(@NonNull UnaryOperator<InMemoryCacheConfig> configFunc) {
        InMemoryCacheConfig cacheConfig = configFunc.apply(new InMemoryCacheConfig());
        this.cacheManager = new InMemoryCacheManager<>(cacheConfig);
        return self();
    }

    /**
     * Creates new cache instance for each client built,
     * use {@link WebClientBuilderBaseWithCache#cache(CacheManager)} if you want shared cache instance.
     */
    public SELF inMemoryCache(@NonNull InMemoryCacheConfig config) {
        this.cacheManager = new InMemoryCacheManager<>(config);
        return self();
    }
}
