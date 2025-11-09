package kameleoon.weathersdksrc;

import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.ApiRequestTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThan;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenWeatherDefaultClientTest extends WeatherClientTestBase<WeatherApiStandardClient> {

    @BeforeEach
    void buildClient() {
        client = clientBuilder().build();
    }

    @Test
    void syncShouldReturnWeatherInfo_onSuccessResponse() {
        stabWeather200();

        WeatherInfo info = client.fetchWeather(TEST_CITY);

        assertNotNull(info);
        assertEquals(TEST_CITY.nameLike(), info.getName());
        assertEquals("Clouds", info.getWeather().main());
    }

    @Test
    void shouldThrowException_onErrorResponse_fromWeatherApi() {
        stabWeather400();

        CityName city = new CityName("UnknownCity");

        Exception ex = assertThrows(ApiClientException.class, () -> client.fetchWeather(city));
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void shouldUseCache() {
        stabWeather200();

        WeatherInfo first = client.fetchWeather(TEST_CITY);
        WeatherInfo second = client.fetchWeather(TEST_CITY);

        verify(1, getRequestedFor(urlPathMatching(weatherPathRegex())));
        assertSame(first, second);
    }

    @Test
    void shouldTimeout_ifWeatherApiHangs() {
        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(aResponse()
                        .withFixedDelay((int) Duration.ofSeconds(5).toMillis()) // 5s delay, timeout = 1s
                        .withBody("{}")));

        ApiRequestTimeoutException openWeatherApiError = assertThrows(ApiRequestTimeoutException.class, () -> client.fetchWeather(TEST_CITY));
        assertInstanceOf(HttpTimeoutException.class, openWeatherApiError.getCause());
    }

    @Test
    void shouldUpdateCacheInPollingMode() throws Exception {
        client.close();

        var POLLING_INTERVAL_MS = 500;
        var tempBeforePoll = 25;
        var tempAfterPoll = -25;

        WeatherApiStandardClient pollingClient = clientBuilder()
                .mode(Mode.POLLING_CACHE)
                .pollInterval(Duration.ofMillis(POLLING_INTERVAL_MS))
                .pollTimeout(Duration.ofSeconds(5))
                .build();

        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(okJson(String.format("{\"weather\": [{\"main\": \"Clouds\"}], \"main\": {\"temp\": %s}}", tempBeforePoll))));
        WeatherInfo beforePoll = pollingClient.fetchWeather(TEST_CITY);
        assertEquals(tempBeforePoll, beforePoll.getTemperature().getTemp());
        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(okJson(String.format("{\"weather\": [{\"main\": \"Clouds\"}], \"main\": {\"temp\": %s}}", tempAfterPoll))));

        Thread.sleep(POLLING_INTERVAL_MS + 500);

        verify(moreThan(1), getRequestedFor(urlPathMatching(weatherPathRegex())));
        WeatherInfo afterPoll = pollingClient.fetchWeather(TEST_CITY);
        assertEquals(tempAfterPoll, afterPoll.getTemperature().getTemp());

        pollingClient.close();
    }
}
