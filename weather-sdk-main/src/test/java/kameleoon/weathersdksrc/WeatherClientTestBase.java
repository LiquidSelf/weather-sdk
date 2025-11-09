package kameleoon.weathersdksrc;

import com.github.tomakehurst.wiremock.WireMockServer;
import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.builders.OpenWeatherReactiveClientBuilder;
import kameleoon.weathersdk.builders.OpenWeatherStandardClientBuilder;
import kameleoon.weathersdk.cache.inmemory.InMemoryCacheConfig;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.removeAllMappings;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@Slf4j
public abstract class WeatherClientTestBase<T extends WebApiClient> {

    protected static WireMockServer wireMockServer;

    protected static String TEST_API_KEY = "test_api_key";
    protected static String TEST_HOST = "localhost";
    protected static int TEST_PORT = 8089;

    // see success_weather_response_example.json
    protected static CityName TEST_CITY = CityName.like("Karaganda");
    protected static String WEATHER_PATH = "/data/2.5/weather";

    protected T client;

    @AfterEach
    void cleanup() throws Exception {
        log.info("Cleaning up");
        if (client != null) client.close();
        wireMockServer.resetAll();
        removeAllMappings();
    }

    @BeforeAll
    static void setupServer() {
        log.info("Setting up server");
        wireMockServer = new WireMockServer(TEST_PORT);
        wireMockServer.start();
        configureFor(TEST_HOST, TEST_PORT);
        log.info("Server started on port {}", TEST_PORT);
    }

    @AfterAll
    static void stopServer() {
        if (wireMockServer != null) wireMockServer.stop();
    }

    protected OpenWeatherStandardClientBuilder clientBuilder() {
        return WeatherApiStandardClient.builder()
                .mode(Mode.REQUEST_CACHE)
                .baseUrl(weatherUrl())
                .responseTimeout(Duration.ofSeconds(1))
                .apiKey(TEST_API_KEY)
                .inMemoryCache(InMemoryCacheConfig.of(10L, Duration.ofSeconds(10)));
    }

    public OpenWeatherReactiveClientBuilder reactiveClientBuilder() {
        return WeatherApiReactiveClient.builder()
                .mode(Mode.REQUEST_CACHE)
                .baseUrl(weatherUrl())
                .responseTimeout(Duration.ofSeconds(1))
                .apiKey(TEST_API_KEY)
                .inMemoryCache(InMemoryCacheConfig.of(10L, Duration.ofSeconds(10)));
    }

    protected static void stabWeather200() {
        stubFor(get(urlPathEqualTo(WEATHER_PATH))
                .withQueryParam("q", equalTo(TEST_CITY.nameLike()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("success_weather_response_example.json")));
    }

    protected static void stabWeather400() {
        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"bad request\"}")));
    }

    protected static String weatherUrl() {
        return wireMockServer.baseUrl() + WEATHER_PATH;
    }

    protected static String weatherPathRegex() {
        return WEATHER_PATH + ".*";
    }
}
