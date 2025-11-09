package kameleoon.weathersdk.builders;

import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.internal.builder.standard.WebClientBuilderBaseWithCache;
import kameleoon.weathersdk.internal.httpclients.openweather.weather.standard.OpenWeatherHttpClient;
import kameleoon.weathersdk.internal.httpclients.openweather.weather.standard.OpenWeatherHttpClientWithCache;
import kameleoon.weathersdk.internal.registry.OpenWeatherClientRegistrar;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * <p><b>Client build example</b>
 * {@snippet :
 * WeatherApiStandardClient client = WeatherApiStandardClient.builder()
 *         .mode(Mode.REQUEST_CACHE)
 *         .baseUrl(weatherUrl())
 *         .responseTimeout(Duration.ofSeconds(5))
 *         .apiKey(TEST_API_KEY)
 *         .inMemoryCache(InMemoryCacheConfig.of(10L, Duration.ofSeconds(10)))
 *         .build();
 *}
 * @see WebClientBuilderBaseWithCache
 * @see kameleoon.weathersdk.internal.builder.WebClientBuilderBase
 */
@NoArgsConstructor
public final class OpenWeatherStandardClientBuilder
        extends WebClientBuilderBaseWithCache<OpenWeatherStandardClientBuilder, WeatherInfo, WeatherApiStandardClient>
        implements OpenWeatherClientRegistrar<WeatherApiStandardClient> {

    @Override
    public WeatherApiStandardClient build() {
        return tryRegisterClient(apiKey, () ->
                switch (mode) {
                    case NO_CACHE -> new OpenWeatherHttpClient(
                            baseUrl,
                            apiKey,
                            responseTimeout,
                            () -> unregisterClient(apiKey)
                    );
                    case REQUEST_CACHE, POLLING_CACHE -> new OpenWeatherHttpClientWithCache(
                            mode,
                            baseUrl,
                            apiKey,
                            responseTimeout,
                            () -> unregisterClient(apiKey),
                            cacheManager,
                            pollInterval,
                            pollTimeout
                    );
                }
        );
    }

    public static OpenWeatherStandardClientBuilder ofDefaults(String apiKey, String baseUrl) {
        return OpenWeatherStandardClientBuilder.create()
                .mode(Mode.NO_CACHE)
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .responseTimeout(Duration.ofSeconds(5));
    }

    public static OpenWeatherStandardClientBuilder create() {
        return new OpenWeatherStandardClientBuilder();
    }

    @Override
    protected OpenWeatherStandardClientBuilder self() {
        return this;
    }
}
