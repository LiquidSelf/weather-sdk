package kameleoon.weathersdk.internal.httpclients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.ApiRequestTimeoutException;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static kameleoon.weathersdk.internal.utils.HttpUtils.maskApiKey;
import static kameleoon.weathersdk.internal.utils.HttpUtils.throwOnBadStatus;
import static kameleoon.weathersdk.internal.utils.HttpUtils.unwrapCompletionException;

@Slf4j
public abstract class AbstractHttpClient implements WebApiClient {

    protected final String baseUrl;
    protected final String apiKey;
    protected final Duration responseTimeout;
    protected final ObjectMapper objectMapper;
    protected final HttpClient httpClient;
    protected final AtomicBoolean closed = new AtomicBoolean(false);

    protected AbstractHttpClient(@NonNull String baseUrl,
                                 @NonNull String apiKey,
                                 @Nullable Duration responseTimeout) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.responseTimeout = responseTimeout != null ? responseTimeout : Duration.ofSeconds(10);
        this.objectMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    protected <T> T sendSync(HttpRequest request, Class<T> responseType, @Nullable String contextInfo) throws ApiClientException {
        if (closed.get()) {
            throw new ApiClientException("Client is closed");
        }
        try {
            return trySendSync(request, responseType, contextInfo);
        } catch (HttpTimeoutException e) {
            throw new ApiRequestTimeoutException("Request timeout after " + responseTimeout, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiClientException("Request interrupted", e);
        } catch (Exception e) {
            throw new ApiClientException("Api call error: " + e.getMessage(), e);
        }
    }

    protected <T> T trySendSync(HttpRequest request, Class<T> responseType, @Nullable String contextInfo) throws ApiClientException, IOException, InterruptedException {
        try {
            URI safeUri = maskApiKey(request.uri());
            log.debug("Executing GET request to {}", safeUri);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            throwOnBadStatus(response, contextInfo);
            log.debug("Response status: {}", response.statusCode());
            return objectMapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            log.error("Exception occurred while sending request", e);
            throw e;
        }
    }

    protected <T> CompletableFuture<T> sendAsync(HttpRequest request, Class<T> responseType, @Nullable String contextInfo) {
        if (closed.get()) return CompletableFuture.failedFuture(new ApiClientException("Client is closed"));

        URI safeUri = maskApiKey(request.uri());
        log.debug("Preparing request to {}", safeUri);
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    throwOnBadStatus(response, contextInfo);
                    log.debug("Response status: {}", response.statusCode());
                    try {
                        return objectMapper.readValue(response.body(), responseType);
                    } catch (IOException e) {
                        throw new ApiClientException("Failed to parse API response", e);
                    }
                })
                .exceptionallyCompose(ex -> {
                    Throwable root = unwrapCompletionException(ex);
                    log.error("Failed to call API {}: {}", safeUri, root.toString());
                    switch (root) {
                        case HttpTimeoutException httpTimeoutException -> {
                            return CompletableFuture.failedFuture(new ApiRequestTimeoutException("Request timeout after " + responseTimeout, httpTimeoutException));
                        }
                        case ApiClientException apiClientException -> {
                            return CompletableFuture.failedFuture(apiClientException);
                        }
                        case InterruptedException interruptedException -> {
                            Thread.currentThread().interrupt();
                            return CompletableFuture.failedFuture(new ApiClientException("Request interrupted", interruptedException));
                        }
                        default -> {
                            return CompletableFuture.failedFuture(new ApiClientException("Unexpected error calling API", root));
                        }
                    }
                });
    }

    protected HttpRequest buildGetRequest(URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(uri);
        if (responseTimeout != null) builder.timeout(responseTimeout);
        return builder.build();
    }

    @Override
    public String apiKey() {
        return apiKey;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            log.debug("Closing {}", getClass().getSimpleName());
            httpClient.close();
        }
    }
}
