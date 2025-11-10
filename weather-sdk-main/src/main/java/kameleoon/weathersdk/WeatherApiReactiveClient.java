package kameleoon.weathersdk;

import kameleoon.weathersdk.builders.OpenWeatherReactiveClientBuilder;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.NonNull;
import reactor.core.publisher.Mono;

/**
 * Weather API reactive client,
 * To build OpenWeather client use {@link #builder()}
 *
 * @see OpenWeatherReactiveClientBuilder
 */
public interface WeatherApiReactiveClient extends WebApiClient {

    Mono<WeatherInfo> fetchWeather(@NonNull CityName city);

    static OpenWeatherReactiveClientBuilder builder() {
        return OpenWeatherReactiveClientBuilder.create();
    }
}
