package kameleoon.weathersdk;

import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.cache.inmemory.InMemoryPreconfiguredCacheHolder;
import lombok.NonNull;

import java.time.Duration;

public class WeatherClients {
    private WeatherClients() {
        // prevent instantiation
    }

    public static WeatherApiStandardClient createSimpleClient(@NonNull String apiKey, @NonNull String weatherBaseUrl
    ) {
        return WeatherApiStandardClient.builder()
                .mode(Mode.REQUEST_CACHE)
                .apiKey(apiKey)
                .baseUrl(weatherBaseUrl)
                .responseTimeout(Duration.ofSeconds(10))
                .cache(InMemoryPreconfiguredCacheHolder.getInstance())
                .build();
    }
}
