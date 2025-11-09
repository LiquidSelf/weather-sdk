package kameleoon.weathersdksrc;

import kameleoon.weathersdk.WeatherApiReactiveClient;
import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.dto.out.WeatherInfo;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.ApiRequestTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class OpenWeatherReactiveClientTest extends WeatherClientTestBase<WeatherApiReactiveClient> {

    @BeforeEach
    void buildClient() {
        client = reactiveClientBuilder().build();
    }

    @Test
    void syncShouldReturnWeatherInfo_onSuccessResponse() {
        stabWeather200();

        StepVerifier.create(client.fetchWeather(TEST_CITY))
                .expectNextMatches(info ->
                        info.getName().equals(TEST_CITY.nameLike())
                        && info.getWeather().main().equals("Clouds")
                )
                .verifyComplete();
    }

    @Test
    void shouldError_whenWeatherApiReturnsBadRequest() {
        stabWeather400();

        StepVerifier.create(client.fetchWeather(TEST_CITY))
                .expectErrorMatches(err -> err instanceof ApiClientException &&
                                           err.getMessage().contains("bad request"))
                .verify();
    }

    @Test
    void shouldUseCache() {
        stabWeather200();

        WeatherInfo first = client.fetchWeather(TEST_CITY).block();
        WeatherInfo second = client.fetchWeather(TEST_CITY).block();

        verify(1, getRequestedFor(urlPathMatching(weatherPathRegex())));
        assertSame(first, second);
    }

    @Test
    void shouldTimeout_ifWeatherApiHangs() {
        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(aResponse()
                        .withFixedDelay((int) Duration.ofSeconds(5).toMillis()) // 5s delay, timeout = 1s
                        .withBody("{}")));

        StepVerifier.create(client.fetchWeather(TEST_CITY))
                .expectErrorMatches(err -> {
                    Throwable root = Exceptions.unwrap(err);
                    assertInstanceOf(ApiRequestTimeoutException.class, root);
                    return root.getCause() instanceof TimeoutException;
                })
                .verify();
    }

    @Test
    void shouldEmitUpdatedWeatherAfterPollingInterval() throws Exception {
        client.close();

        var POLLING_INTERVAL_MS = 500;
        var tempBeforePoll = 25;
        var tempAfterPoll = -25;

        WeatherApiReactiveClient pollingClient = reactiveClientBuilder()
                .mode(Mode.POLLING_CACHE)
                .pollInterval(Duration.ofMillis(POLLING_INTERVAL_MS))
                .pollTimeout(Duration.ofSeconds(5))
                .build();


        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(okJson(String.format("{\"weather\": [{\"main\": \"Clouds\"}], \"main\": {\"temp\": %s}}", tempBeforePoll))));
        WeatherInfo beforePoll = pollingClient.fetchWeather(TEST_CITY).block();
        assertEquals(tempBeforePoll, beforePoll.getTemperature().getTemp());

        stubFor(get(urlPathMatching(weatherPathRegex()))
                .willReturn(okJson(String.format("{\"weather\": [{\"main\": \"Clouds\"}], \"main\": {\"temp\": %s}}", tempAfterPoll))));

        //VirtualTimeScheduler vts = VirtualTimeScheduler.create();
        Thread.sleep(POLLING_INTERVAL_MS * 2);
        WeatherInfo after = pollingClient.fetchWeather(TEST_CITY).block();
        assertEquals(tempAfterPoll, after.getTemperature().getTemp());

        pollingClient.close();
    }

}
