package kameleoon.weathersdkstarter.autoconfigure.cache;

import kameleoon.weathersdk.cache.CacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheReactiveAdapter;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdkstarter.properties.OpenWeatherApiConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@ConditionalOnClass(Mono.class)
@ConditionalOnBean(CacheManager.class)
@EnableConfigurationProperties(OpenWeatherApiConfigProperties.class)
@ConditionalOnProperty(value = "openweather.client.reactive", havingValue = "true")
@ConditionalOnExpression("('${openweather.client.mode:NO_CACHE}' != 'NO_CACHE')")
public class ReactiveCacheManagerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "weatherReactiveInMemoryCache")
    @ConditionalOnProperty(value = "openweather.client.in-memory-cache.cache-record-ttl")
    public InMemoryCacheReactiveAdapter<WeatherInfo> weatherReactiveInMemoryCache(InMemoryCacheManager<WeatherInfo> cacheManager) {
        log.info("Configuring in memory reactive cache for WeatherInfo");
        return new InMemoryCacheReactiveAdapter<>(cacheManager);
    }
}
