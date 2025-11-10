package kameleoon.weathersdkstarter.autoconfigure.client.weather;

import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.cache.ReactiveCacheManager;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdkstarter.properties.OpenWeatherApiConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@ConditionalOnClass(Mono.class)
@EnableConfigurationProperties(OpenWeatherApiConfigProperties.class)
@ConditionalOnProperty(value = "openweather.client.reactive", havingValue = "true")
public class WeatherApiReactiveClientAutoConfiguration {

    @Bean
    @ConditionalOnExpression("('${openweather.client.mode:NO_CACHE}' != 'NO_CACHE')")
    public WeatherApiReactiveClient reactiveClient(OpenWeatherApiConfigProperties properties,
                                                   ReactiveCacheManager<WeatherInfo> cacheManager
    ) {
        log.info("Configuring WeatherApiReactiveClient");
        return WeatherApiReactiveClient.builder()
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
    public WeatherApiReactiveClient reactiveClientNoCache(OpenWeatherApiConfigProperties properties) {
        log.info("Configuring WeatherApiReactiveClient with no cache");
        return WeatherApiReactiveClient.builder()
                .mode(properties.getMode())
                .baseUrl(properties.getBaseUrl())
                .apiKey(properties.getApiKey())
                .responseTimeout(properties.getResponseTimeout())
                .pollTimeout(properties.getPollTimeout())
                .pollInterval(properties.getPollInterval())
                .build();
    }
}
