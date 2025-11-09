package kameleoon.weathersdkstarter.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kameleoon.weathersdk.builders.Mode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static kameleoon.weathersdk.builders.Mode.NO_CACHE;

@Slf4j
@Data
@Validated
@ConfigurationProperties(prefix = "openweather.client")
public class OpenWeatherApiConfigProperties {

    private boolean reactive = false;

    @NotNull
    private Mode mode = NO_CACHE;
    @NotBlank
    private String baseUrl;
    @NotBlank
    private String apiKey;
    @NotNull
    private Duration responseTimeout = Duration.ofSeconds(30);

    @Valid
    @NestedConfigurationProperty
    private InMemoryCacheProps inMemoryCache;

    private Duration pollInterval;
    private Duration pollTimeout;

}