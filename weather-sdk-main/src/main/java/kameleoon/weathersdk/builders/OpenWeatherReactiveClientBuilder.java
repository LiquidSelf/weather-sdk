package kameleoon.weathersdk.builders;

import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.internal.builder.reactive.WebClientBuilderBaseWithReactiveCache;
import kameleoon.weathersdk.internal.httpclients.openweather.weather.reactive.OpenWeatherReactiveHttpClient;
import kameleoon.weathersdk.internal.httpclients.openweather.weather.reactive.OpenWeatherReactiveHttpClientWithCache;
import kameleoon.weathersdk.internal.registry.OpenWeatherClientRegistrar;
import lombok.NoArgsConstructor;

/**
 * Builder used to build {@link WeatherApiReactiveClient}
 *
 * <p><b>Client build example</b>
 * {@snippet :
 * WeatherApiReactiveClient reactiveClient = WeatherApiReactiveClient.builder()
 *        .mode(Mode.REQUEST_CACHE)
 *        .baseUrl(weatherUrl())
 *        .responseTimeout(Duration.ofSeconds(1))
 *        .apiKey(TEST_API_KEY)
 *        .inMemoryCache(InMemoryCacheConfig.of(10L, Duration.ofSeconds(10)))
 * .build();}
 *
 * @see WebClientBuilderBaseWithReactiveCache
 * @see kameleoon.weathersdk.internal.builder.WebClientBuilderBase
 */
@NoArgsConstructor
public final class OpenWeatherReactiveClientBuilder
        extends WebClientBuilderBaseWithReactiveCache<OpenWeatherReactiveClientBuilder, WeatherInfo, WeatherApiReactiveClient>
        implements OpenWeatherClientRegistrar<WeatherApiReactiveClient> {

    @Override
    public WeatherApiReactiveClient build() {
        return tryRegisterClient(apiKey, () ->
                switch (mode) {
                    case NO_CACHE -> new OpenWeatherReactiveHttpClient(
                            baseUrl,
                            apiKey,
                            responseTimeout,
                            () -> unregisterClient(apiKey)
                    );
                    case REQUEST_CACHE, POLLING_CACHE -> new OpenWeatherReactiveHttpClientWithCache(
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

    public static OpenWeatherReactiveClientBuilder create() {
        return new OpenWeatherReactiveClientBuilder();
    }

    @Override
    protected OpenWeatherReactiveClientBuilder self() {
        return this;
    }
}