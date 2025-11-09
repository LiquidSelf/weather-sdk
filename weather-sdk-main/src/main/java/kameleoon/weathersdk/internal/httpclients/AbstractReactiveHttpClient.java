package kameleoon.weathersdk.internal.httpclients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.ApiRequestTimeoutException;
import kameleoon.weathersdk.exceptions.UnauthorizedException;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static kameleoon.weathersdk.internal.utils.HttpUtils.maskApiKey;

@Slf4j
public abstract class AbstractReactiveHttpClient<T> implements WebApiClient {

    protected final String baseUrl;
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final Duration responseTimeout;
    protected final String apiKey;
    protected final Runnable closeCallback;
    protected final AtomicBoolean closed = new AtomicBoolean(false);

    protected AbstractReactiveHttpClient(@NonNull String baseUrl,
                                         @NonNull String apiKey,
                                         @NonNull Duration responseTimeout,
                                         @NonNull Runnable closeCallback

    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.responseTimeout = responseTimeout;
        this.closeCallback = closeCallback;
        this.objectMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        this.httpClient = HttpClient.create().baseUrl(baseUrl);
    }

    protected Mono<T> executeGet(URI uri, Class<T> responseType) {
        if (closed.get()) return Mono.error(new ApiClientException("Client is closed"));
        URI safeUri = maskApiKey(uri);
        return httpClient.get()
                .uri(uri)
                .responseSingle((response, bytes) -> {
                    int status = response.status().code();
                    log.debug("Response status {}", status);
                    if (status == 401) {
                        return Mono.error(new UnauthorizedException("Unauthorized: invalid API key"));
                    } else if (status >= 400) {
                        return bytes.asString()
                                .defaultIfEmpty("")
                                .flatMap(body ->
                                        Mono.error(new ApiClientException(String.format("HTTP %s: %s", status, body)))
                                );
                    }
                    return bytes.asString();
                })
                .flatMap(body -> parseBody(body, responseType))
                .timeout(responseTimeout)
                .onErrorMap(TimeoutException.class,
                        ex -> new ApiRequestTimeoutException("Request timed out after " + responseTimeout, ex)
                )
                .doOnSubscribe(sub -> log.debug("Calling API: {}", safeUri))
                .doOnError(ex -> log.error("Error calling API {}", safeUri, ex))
                .doOnSuccess(resp -> log.debug("Successfully fetched weather data from {}", safeUri));
    }

    protected Mono<T> parseBody(String body, Class<T> responseType) {
        try {
            return Mono.just(objectMapper.readValue(body, responseType));
        } catch (Exception e) {
            log.error("Failed to parse response", e);
            return Mono.error(new ApiClientException("Failed to parse response", e));
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            log.debug("Closing {}", getClass().getSimpleName());
            closeCallback.run();
        }
    }
}
