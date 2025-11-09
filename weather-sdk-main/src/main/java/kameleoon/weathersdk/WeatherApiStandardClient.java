package kameleoon.weathersdk;


import kameleoon.weathersdk.builders.OpenWeatherStandardClientBuilder;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Weather API reactive client,
 * To build default client use {@link #builder()}
 *
 * @see OpenWeatherStandardClientBuilder
 */
public interface WeatherApiStandardClient extends WebApiClient {

    WeatherInfo fetchWeather(@NonNull CityName city);
    CompletableFuture<WeatherInfo> fetchWeatherAsync(@NonNull CityName city);

    static OpenWeatherStandardClientBuilder builder() {
        return OpenWeatherStandardClientBuilder.create();
    }
}
