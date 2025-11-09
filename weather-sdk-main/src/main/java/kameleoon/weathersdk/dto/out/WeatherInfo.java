package kameleoon.weathersdk.dto.out;

import kameleoon.weathersdk.dto.out.add.Sys;
import kameleoon.weathersdk.dto.out.add.Temperature;
import kameleoon.weathersdk.dto.out.add.WeatherDescription;
import kameleoon.weathersdk.dto.out.add.Wind;
import kameleoon.weathersdk.internal.dto.OpenWeatherApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public final class WeatherInfo {
    private final WeatherDescription weather;
    private final Temperature temperature;
    private final int visibility;
    private final Wind wind;
    private final Long dateTime;
    private final Sys sys;
    private final long timezone;
    private final String name;

    public static WeatherInfo of(OpenWeatherApiResponse apiResponse) {
        OpenWeatherApiResponse.Weather apiWeather = apiResponse.getWeather().getFirst(); // ?
        return WeatherInfo.builder()
                .weather(new WeatherDescription(apiWeather.main(), apiWeather.description()))
                .temperature(new Temperature(apiResponse.temp(), apiResponse.feelsLike()))
                .visibility(apiResponse.getVisibility())
                .wind(new Wind(apiResponse.windSpeed()))
                .dateTime(apiResponse.getDt())
                .sys(new Sys(apiResponse.sunrise(), apiResponse.sunset()))
                .timezone(apiResponse.getTimezone())
                .name(apiResponse.getName())
                .build();
    }
}