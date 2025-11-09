package kameleoon.weathersdk.internal.builder.reactive;

import kameleoon.weathersdk.cache.ReactiveCacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheConfig;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheReactiveAdapter;
import kameleoon.weathersdk.cache.inmemory.InMemoryPreconfiguredCacheHolder;
import kameleoon.weathersdk.internal.builder.WebClientBuilderBase;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.function.UnaryOperator;

/**
 * Abstract builder providing functionality to build cache part of client
 *
 * @see WebClientBuilderBase
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class WebClientBuilderBaseWithReactiveCache<SELF, CACHE_TYPE, CLIENT_TYPE extends WebApiClient>
        extends WebClientBuilderBase<SELF, CLIENT_TYPE> {

    protected ReactiveCacheManager<CACHE_TYPE> cacheManager;

    /**
     * @see InMemoryPreconfiguredCacheHolder
     */
    public SELF cache(@NonNull ReactiveCacheManager<CACHE_TYPE> cacheManager) {
        this.cacheManager = cacheManager;
        return self();
    }

    /**
     * Uses inmemory cache wrapped in reactive wrapper, creates new instance of cache for each built client,
     * use {@link WebClientBuilderBaseWithReactiveCache#cache(ReactiveCacheManager)} if you want a shared cache instance.
     *
     * @return cache wrapped in reactive adapter
     */
    public SELF inMemoryCache(@NonNull UnaryOperator<InMemoryCacheConfig> configFunc) {
        InMemoryCacheConfig cacheConfig = configFunc.apply(new InMemoryCacheConfig());
        this.cacheManager = new InMemoryCacheReactiveAdapter<>(new InMemoryCacheManager<>(cacheConfig));
        return self();
    }

    /**
     * Uses inmemory cache wrapped in reactive wrapper, creates new instance of cache for each built client,
     * use {@link WebClientBuilderBaseWithReactiveCache#cache(ReactiveCacheManager)} if you want a shared cache instance.
     *
     * @return wrapped cache
     */
    public SELF inMemoryCache(@NonNull InMemoryCacheConfig config) {
        this.cacheManager = new InMemoryCacheReactiveAdapter<>(new InMemoryCacheManager<>(config));
        return self();
    }
}
