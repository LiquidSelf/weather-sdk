package kameleoon.weathersdkstarter.autoconfigure.client.weather;

import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.cache.CacheManager;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdkstarter.properties.OpenWeatherApiConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenWeatherApiConfigProperties.class)
@ConditionalOnProperty(value = "openweather.client.reactive", havingValue = "false", matchIfMissing = true)
public class WeatherApiClientAutoConfiguration {

    @Bean
    @ConditionalOnExpression("('${openweather.client.mode:NO_CACHE}' != 'NO_CACHE')")
    public WeatherApiStandardClient standardClient(OpenWeatherApiConfigProperties properties,
                                                   CacheManager<WeatherInfo> cacheManager
    ) {
        log.info("Configuring WeatherApiStandardClient");
        return WeatherApiStandardClient.builder()
                .mode(properties.getMode())
                .baseUrl(properties.getBaseUrl())
                .apiKey(properties.getApiKey())
                .responseTimeout(properties.getResponseTimeout())
                .cache(cacheManager)
                .pollTimeout(properties.getPollTimeout())
                .pollInterval(properties.getPollInterval())
                .build();
    }

    @Bean
    @ConditionalOnExpression("('${openweather.client.mode:NO_CACHE}' == 'NO_CACHE')")
    public WeatherApiStandardClient standardClientNoCache(OpenWeatherApiConfigProperties properties) {
        log.info("Configuring WeatherApiStandardClient without cache");
        return WeatherApiStandardClient.builder()
                .mode(properties.getMode())
                .baseUrl(properties.getBaseUrl())
                .apiKey(properties.getApiKey())
                .responseTimeout(properties.getResponseTimeout())
                .pollTimeout(properties.getPollTimeout())
                .pollInterval(properties.getPollInterval())
                .build();
    }
}