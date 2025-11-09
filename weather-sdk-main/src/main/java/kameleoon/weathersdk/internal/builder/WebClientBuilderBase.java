package kameleoon.weathersdk.internal.builder;

import kameleoon.weathersdk.builders.Mode;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;

/**
 * Base abstract builder providing common configuration options
 * for building {@link WebApiClient} implementations.
 * <p>
 * Defines shared fields and fluent setters for typical Web API client settings
 * such as base URL, API key, request/response timeouts, and polling parameters.
 *
 * @param <SELF>        the concrete builder type, enabling fluent method chaining
 * @param <CLIENT_TYPE> the resulting client type built by this builder
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class WebClientBuilderBase<SELF, CLIENT_TYPE extends WebApiClient> extends SelfAwareBuilder<SELF, CLIENT_TYPE> {

    /**
     * Operating mode of the client (e.g. cache behavior).
     * Defaults to {@link Mode#NO_CACHE}.
     */
    protected Mode mode = Mode.NO_CACHE;

    /**
     * Base URL of the target API endpoint.
     */
    protected String baseUrl;

    /**
     * API key used for authenticating requests.
     */
    protected String apiKey;

    /**
     * Maximum allowed duration for waiting for a response.
     */
    protected Duration responseTimeout;

    /**
     * Polling interval, when in {@link Mode#POLLING_CACHE} mode.
     */
    protected Duration pollInterval;

    /**
     * Polling timeout, when in {@link Mode#POLLING_CACHE} mode.
     */
    protected Duration pollTimeout;


    public SELF baseUrl(@NonNull String baseUrl) {
        this.baseUrl = baseUrl;
        return self();
    }

    public SELF apiKey(@NonNull String apiKey) {
        this.apiKey = apiKey;
        return self();
    }

    public SELF responseTimeout(@NonNull Duration responseTimeout) {
        this.responseTimeout = responseTimeout;
        return self();
    }

    public SELF mode(@NonNull Mode mode) {
        this.mode = mode;
        return self();
    }

    public SELF pollInterval(@NonNull Duration pollInterval) {
        this.pollInterval = pollInterval;
        return self();
    }

    public SELF pollTimeout(@NonNull Duration pollTimeout) {
        this.pollTimeout = pollTimeout;
        return self();
    }

}
