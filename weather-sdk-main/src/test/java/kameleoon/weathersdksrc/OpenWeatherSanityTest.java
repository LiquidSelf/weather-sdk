package kameleoon.weathersdksrc;

import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.builders.OpenWeatherStandardClientBuilder;
import kameleoon.weathersdk.exceptions.ApiKeyAlreadyInUseError;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class OpenWeatherSanityTest extends WeatherClientTestBase<WeatherApiStandardClient> {

    @BeforeEach
    void buildClient() {
        client = OpenWeatherStandardClientBuilder.ofDefaults(TEST_API_KEY, weatherUrl()).build();
    }

    @Test
    void shouldNotBuildSecondClientsWithSameApiKey() {
        assertNotNull(client);
        assertEquals(client.apiKey(), TEST_API_KEY);

        assertThrows(ApiKeyAlreadyInUseError.class,
                () -> OpenWeatherStandardClientBuilder.ofDefaults(TEST_API_KEY, weatherUrl()).build()
        );
    }

    @Test
    void shouldCreateClientAfterApiKeyReleased() throws Exception {
        assertNotNull(client);
        assertEquals(client.apiKey(), TEST_API_KEY);

        client.close();
        WeatherApiStandardClient client2 = OpenWeatherStandardClientBuilder.ofDefaults(TEST_API_KEY, weatherUrl()).build();
        assertNotNull(client2);
        client2.close();
    }
}
