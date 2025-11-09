package kameleoon.weathersdk.internal.httpclients.openweather.weather.standard;

import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.cache.CacheManager;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.UnauthorizedException;
import kameleoon.weathersdk.internal.poller.CachePoller;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class OpenWeatherHttpClientWithCache extends OpenWeatherHttpClient {

    private final CacheManager<WeatherInfo> cacheManager;
    private CachePoller<WeatherInfo> cachePoller ;

    public OpenWeatherHttpClientWithCache(@NonNull Mode mode,
                                          @NonNull String baseUrl,
                                          @NonNull String apiKey,
                                          @NonNull Duration responseTimeout,
                                          @NonNull Runnable closeCallback,
                                          @NonNull CacheManager<WeatherInfo> cacheManager,
                                          Duration pollInterval,
                                          Duration pollTimeout
    ) {
        super(baseUrl, apiKey, responseTimeout, closeCallback);
        switch (mode) {
            case NO_CACHE -> throw new IllegalArgumentException("OpenWeatherCachePollingHttpClient designed to work with cache");
            case REQUEST_CACHE -> log.debug("Working in REQUEST_CACHE mode");
            case POLLING_CACHE -> {
                log.debug("Working in POLLING_CACHE mode");
                cachePoller = new CachePoller<>(
                        Objects.requireNonNull(pollInterval, "In Polling mode pollInterval cannot be null"),
                        Objects.requireNonNull(pollTimeout, "In Polling mode pollTimeout cannot be null"),
                        cacheManager,
                        cityName -> super.fetchWeatherAsync(CityName.like(cityName))
                );
                cachePoller.start();
            }
        }
        this.cacheManager = cacheManager;
    }

    @Override
    public WeatherInfo fetchWeather(@NonNull CityName city) throws UnauthorizedException, ApiClientException {
        return cacheManager.fetchIfAbsent(
                city.nameLike(),
                cityName -> super.fetchWeather(city)
        );
    }

    @Override
    public CompletableFuture<WeatherInfo> fetchWeatherAsync(@NonNull CityName city) {
        return cacheManager.getAsync(city.nameLike())
                .thenCompose(cached -> {
                    if (cached == null) {
                        return super.fetchWeatherAsync(city)
                                .thenApply(fetched -> {
                                    cacheManager.put(city.nameLike(), fetched);
                                    return fetched;
                                });
                    }
                    log.debug("Returning cached value for {}", city.nameLike());
                    return CompletableFuture.completedFuture(cached);
                });
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            log.debug("Closing {}", getClass().getSimpleName());
            closeCallback.run();
            httpClient.close();
            if (cachePoller != null) cachePoller.shutdown();
        }
    }
}
