package kameleoon.weathersdk.internal.utils;

import kameleoon.weathersdk.exceptions.ApiClientException;
import kameleoon.weathersdk.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.net.URIBuilder;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class HttpUtils {
    private HttpUtils() {
        // prevent instantiation
    }

    public static void throwOnBadStatus(HttpResponse<String> response, @Nullable String contextInfo) throws ApiClientException {
        int status = response.statusCode();
        switch (status) {
            case HttpStatus.SC_UNAUTHORIZED:
                throw new UnauthorizedException("Invalid API key");
            case HttpStatus.SC_NOT_FOUND:
                throw new ApiClientException("Not found: " + (contextInfo != null ? contextInfo : "unknown"));
            default: {
                if (status >= 400) throw new ApiClientException("OpenWeather API returned " + status + " status: " + response.body());
            }
        }
    }

    public static Throwable unwrapCompletionException(Throwable ex) {
        if (ex instanceof CompletionException || ex instanceof ExecutionException && ex.getCause() != null) {
            return ex.getCause();
        }
        return ex;
    }

    public static URI maskApiKey(URI uri) {
        try {
            String query = uri.getQuery();
            if (query == null || !query.contains("appid=")) {
                return uri;
            }
            String maskedQuery = query.replaceAll("appid=[^&]+", "appid=***");
            return new URIBuilder(uri).setCustomQuery(maskedQuery).build();
        } catch (Exception e) {
            return uri;
        }
    }

}
