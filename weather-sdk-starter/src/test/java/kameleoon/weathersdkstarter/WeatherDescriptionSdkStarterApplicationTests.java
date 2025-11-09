package kameleoon.weathersdkstarter;

import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.WeatherApiStandardClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class WeatherDescriptionSdkStarterApplicationTests {
    @Autowired(required = false)
    private WeatherApiStandardClient client;
    @Autowired(required = false)
    private WeatherApiReactiveClient reactiveClient;

    @Test
    void contextLoads() {
        if (client != null) log.info("Got client {}", client);
        if (reactiveClient != null) log.info("Got reactive client {}", reactiveClient);
    }

}
