package kameleoon.weathersdk.internal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenWeatherApiResponse {

    private List<Weather> weather;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private long id;
    private String name;
    private int cod;

    public double temp() {
        return Optional.ofNullable(main).map(Main::temp).orElse(0.0);
    }

    public double feelsLike() {
        return Optional.ofNullable(main).map(Main::feelsLike).orElse(0.0);
    }

    public double windSpeed() {
        return Optional.ofNullable(wind).map(Wind::speed).orElse(0.0);
    }

    public long sunrise() {
        return Optional.ofNullable(sys).map(Sys::sunrise).orElse(0L);
    }

    public long sunset() {
        return Optional.ofNullable(sys).map(Sys::sunset).orElse(0L);
    }

    public record Main(
            double temp,
            @JsonProperty("feels_like") double feelsLike,
            @JsonProperty("temp_min") double tempMin,
            @JsonProperty("temp_max") double tempMax,
            int pressure,
            int humidity,
            @JsonProperty("sea_level") int seaLevel,
            @JsonProperty("grnd_level") int grndLevel
    ) {
    }

    // @formatter:off
    public record Weather(int id, String main, String description, String icon) { }
    public record Clouds(int all) { }
    public record Wind(double speed, int deg, double gust) { }
    public record Sys(int type, int id, String country, long sunrise, long sunset) { }
    // @formatter:on
}