package kameleoon.weathersdk.internal.httpclients.openweather.weather.reactive;

import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.cache.ReactiveCacheManager;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.internal.poller.ReactiveCachePoller;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class OpenWeatherReactiveHttpClientWithCache extends OpenWeatherReactiveHttpClient {

    protected final ReactiveCacheManager<WeatherInfo> cacheManager;
    protected ReactiveCachePoller<WeatherInfo> cachePoller;

    public OpenWeatherReactiveHttpClientWithCache(@NonNull Mode mode,
                                                  @NonNull String baseUrl,
                                                  @NonNull String apiKey,
                                                  @NonNull Duration responseTimeout,
                                                  @NonNull Runnable closeCallback,
                                                  @NonNull ReactiveCacheManager<WeatherInfo> cacheManager,
                                                  Duration pollInterval,
                                                  Duration pollTimeout
    ) {
        super(baseUrl, apiKey, responseTimeout, closeCallback);
        switch (mode) {
            case NO_CACHE -> throw new IllegalArgumentException("OpenWeatherReactivePollingCacheHttpClient designed to work with cache");
            case REQUEST_CACHE -> log.debug("Working in REQUEST_CACHE mode");
            case POLLING_CACHE -> {
                log.debug("Working in POLLING_CACHE mode");
                cachePoller = new ReactiveCachePoller<>(
                        Objects.requireNonNull(pollInterval, "In Polling mode pollInterval cannot be null"),
                        Objects.requireNonNull(pollTimeout, "In Polling mode pollTimeout cannot be null"),
                        cacheManager,
                        cityName -> super.fetchWeather(CityName.like(cityName))
                );
                cachePoller.start();
            }
        }
        this.cacheManager = cacheManager;
    }

    @Override
    public Mono<WeatherInfo> fetchWeather(@NonNull CityName city) {
        return cacheManager.get(city.nameLike())
                .switchIfEmpty(super.fetchWeather(city)
                        .flatMap(weatherInfo ->
                                cacheManager.put(city.nameLike(), weatherInfo).thenReturn(weatherInfo)
                        )
                );
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            log.debug("Closing {}", getClass().getSimpleName());
            closeCallback.run();
            if (cachePoller != null) cachePoller.shutdown();
        }
    }
}
