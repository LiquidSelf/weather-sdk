package kameleoon.weathersdksrc;

import kameleoon.weathersdk.WeatherApiStandardClient;
import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.ApiRequestTimeoutException;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenWeatherDefaultAsyncClientTest extends WeatherClientTestBase<WeatherApiStandardClient> {

    @BeforeEach
    void buildClient() {
        client = clientBuilder().build();
    }

    @Test
    void syncShouldReturnWeatherInfo_onSuccessResponse() {
        stabWeather200();

        WeatherInfo info = client.fetchWeatherAsync(TEST_CITY).join();

        assertNotNull(info);
        assertEquals(TEST_CITY.nameLike(), info.getName());
        assertEquals("Clouds", info.getWeather().main());
    }

    @Test
    void shouldThrowException_onErrorResponseFromWeatherApi() {
        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"bad request\"}")));

        CompletableFuture<WeatherInfo> future = client.fetchWeatherAsync(CityName.like("UnknownCity"));

        CompletionException ex = assertThrows(CompletionException.class, future::join);

        assertTrue(future.isCompletedExceptionally());
        assertInstanceOf(ApiClientException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("bad request"));
    }

    @Test
    void shouldTimeout_ifWeatherApiHangs() {
        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(aResponse()
                        .withFixedDelay((int) Duration.ofSeconds(5).toMillis()) // 5s delay, timeout = 1s
                        .withBody("{}")));

        CompletableFuture<WeatherInfo> future = client.fetchWeatherAsync(TEST_CITY);

        CompletionException ex = assertThrows(CompletionException.class, future::join);

        assertTrue(future.isCompletedExceptionally());
        assertInstanceOf(ApiRequestTimeoutException.class, ex.getCause());
    }


    @Test
    void shouldUseCache() {
        stabWeather200();

        WeatherInfo first = client.fetchWeatherAsync(TEST_CITY).join();
        WeatherInfo second = client.fetchWeatherAsync(TEST_CITY).join();

        verify(1, getRequestedFor(urlPathMatching(weatherPathRegex())));
        assertSame(first, second);
    }
}
