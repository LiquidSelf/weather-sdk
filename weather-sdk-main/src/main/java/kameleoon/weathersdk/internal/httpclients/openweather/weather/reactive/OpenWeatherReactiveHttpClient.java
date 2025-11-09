package kameleoon.weathersdk.internal.httpclients.openweather.weather.reactive;

import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.internal.dto.OpenWeatherApiResponse;
import kameleoon.weathersdk.internal.httpclients.AbstractReactiveHttpClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static kameleoon.weathersdk.internal.utils.OpenWeatherUtils.buildUri;

@Slf4j
public class OpenWeatherReactiveHttpClient extends AbstractReactiveHttpClient<OpenWeatherApiResponse> implements WeatherApiReactiveClient {

    public OpenWeatherReactiveHttpClient(@NonNull String baseUrl,
                                         @NonNull String apiKey,
                                         @NonNull Duration responseTimeout,
                                         @NonNull Runnable closeCallback
    ) {
        super(baseUrl, apiKey, responseTimeout, closeCallback);
    }


    @Override
    public Mono<WeatherInfo> fetchWeather(@NonNull CityName city) {
        return executeGet(buildUri(baseUrl, apiKey, city), OpenWeatherApiResponse.class).map(WeatherInfo::of);
    }

    @Override
    public String apiKey() {
        return apiKey;
    }
}

