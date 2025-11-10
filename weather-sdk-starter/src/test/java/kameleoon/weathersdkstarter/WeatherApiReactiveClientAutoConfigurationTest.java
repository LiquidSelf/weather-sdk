package kameleoon.weathersdkstarter;


import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdkstarter.autoconfigure.cache.CacheManagerAutoConfiguration;
import kameleoon.weathersdkstarter.autoconfigure.cache.ReactiveCacheManagerAutoConfiguration;
import kameleoon.weathersdkstarter.autoconfigure.client.weather.WeatherApiClientAutoConfiguration;
import kameleoon.weathersdkstarter.autoconfigure.client.weather.WeatherApiReactiveClientAutoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(
        classes = {
                CacheManagerAutoConfiguration.class,
                ReactiveCacheManagerAutoConfiguration.class,
                WeatherApiClientAutoConfiguration.class,
                WeatherApiReactiveClientAutoConfiguration.class
        }
)
@TestPropertySource(properties = { "openweather.client.reactive=true" })
class WeatherApiReactiveClientAutoConfigurationTest {

    @Autowired(required = false)
    WeatherApiReactiveClient reactiveClient;

    @Autowired(required = false)
    WeatherApiStandardClient standardClient;

    @Test
    void shouldCreateReactiveClientOnly() {
        assertThat(reactiveClient)
                .as("Reactive client must be created when reactive=true")
                .isNotNull();
        assertThat(standardClient)
                .as("Standard client must NOT be created when reactive=true")
                .isNull();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (standardClient != null) standardClient.close();
        if (reactiveClient != null) reactiveClient.close();
    }
}
