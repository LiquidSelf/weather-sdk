package kameleoon.weathersdkstarter.autoconfigure.cache;

import kameleoon.weathersdk.cache.CacheManager;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheConfig;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheManager;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdkstarter.properties.OpenWeatherApiConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenWeatherApiConfigProperties.class)
@ConditionalOnExpression("('${openweather.client.mode:NO_CACHE}' != 'NO_CACHE')")
public class CacheManagerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    @ConditionalOnProperty(value = "openweather.client.in-memory-cache.cache-record-ttl")
    public InMemoryCacheManager<WeatherInfo> weatherInMemoryCache(OpenWeatherApiConfigProperties properties) {
        log.info("Configuring in memory cache for WeatherInfo");
        return new InMemoryCacheManager<>(InMemoryCacheConfig.of(
                properties.getInMemoryCache().maxCacheRecords(),
                properties.getInMemoryCache().cacheRecordTtl())
        );
    }
}
