package kameleoon.weathersdk.internal.utils;

import kameleoon.weathersdk.dto.in.CityName;
import kameleoon.weathersdk.exceptions.ApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class OpenWeatherUtils {
    private OpenWeatherUtils() {
        // prevent instantiation
    }

    public static URI buildUri(String baseUrl, String apiKey, CityName cityName) {
        try {
            return new URIBuilder(baseUrl)
                    .addParameter("q", cityName.nameLike())
                    .addParameter("appid", apiKey)
                    .addParameter("units", "metric")
                    .build();
        } catch (URISyntaxException e) {
            log.error("Failed to build URI", e);
            throw new ApiClientException(String.format("Failed to build URI for %s", cityName.nameLike()), e);
        }
    }
}
