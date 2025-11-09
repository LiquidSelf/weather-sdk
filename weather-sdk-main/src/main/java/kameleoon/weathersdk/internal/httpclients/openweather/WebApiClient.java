package kameleoon.weathersdk.internal.httpclients.openweather;

public interface WebApiClient extends AutoCloseable {
    String apiKey();
}
