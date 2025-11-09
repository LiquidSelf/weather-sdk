package kameleoon.weathersdk.cache.inmemory;

import kameleoon.weathersdk.dto.out.WeatherInfo;

import java.time.Duration;

/**
 * preconfigured inner static holder for in memory cache
 */
public final class InMemoryPreconfiguredCacheHolder {

    private InMemoryPreconfiguredCacheHolder() {
        // prevent instantiation
    }

    private static class Holder {
        static final InMemoryCacheManager<WeatherInfo> INSTANCE = new InMemoryCacheManager<>(
                new InMemoryCacheConfig(10L, Duration.ofMinutes(10))
        );
    }

    public static InMemoryCacheManager<WeatherInfo> getInstance() {
        return Holder.INSTANCE;
    }

    public static void reset() {
        synchronized (Holder.class) {
            Holder.INSTANCE.clear();
        }
    }
}