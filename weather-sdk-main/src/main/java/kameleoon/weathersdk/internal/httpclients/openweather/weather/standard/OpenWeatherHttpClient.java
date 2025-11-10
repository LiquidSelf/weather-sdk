package kameleoon.weathersdk.internal.httpclients.openweather.weather.standard;

import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.internal.dto.OpenWeatherApiResponse;
import kameleoon.weathersdk.internal.httpclients.AbstractHttpClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static kameleoon.weathersdk.internal.utils.OpenWeatherUtils.buildUri;

@Slf4j
public class OpenWeatherHttpClient extends AbstractHttpClient implements WeatherApiStandardClient {

    protected final Runnable closeCallback;

    public OpenWeatherHttpClient(@NonNull String baseUrl,
                                 @NonNull String apiKey,
                                 @NonNull Duration responseTimeout,
                                 @NonNull Runnable closeCallback) {
        super(baseUrl, apiKey, responseTimeout);
        this.closeCallback = closeCallback;
    }

    @Override
    public WeatherInfo fetchWeather(@NonNull CityName city) throws ApiClientException {
        return WeatherInfo.of(sendSync(buildGetRequest(buildUri(baseUrl, apiKey, city)), OpenWeatherApiResponse.class, city.nameLike()));
    }

    @Override
    public CompletableFuture<WeatherInfo> fetchWeatherAsync(@NonNull CityName cityName) {
        return sendAsync(buildGetRequest(buildUri(baseUrl, apiKey, cityName)), OpenWeatherApiResponse.class, cityName.nameLike())
                .thenApply(WeatherInfo::of);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            log.debug("Closing {}", getClass().getSimpleName());
            httpClient.close();
            closeCallback.run();
        }
    }
}
