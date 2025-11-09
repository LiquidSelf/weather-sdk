package kameleoon.weathersdk.internal.registry;

import kameleoon.weathersdk.exceptions.ApiKeyAlreadyInUseError;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;
import org.apache.hc.core5.annotation.Internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Internal
class OpenWeatherClientRegistry {

    private static final Map<String, WebApiClient> registry = new HashMap<>();

    static synchronized <T extends WebApiClient> T tryRegisterClient(String apiKey, Supplier<T> clientSupplier) {
        if (registry.containsKey(apiKey)) {
            throw new ApiKeyAlreadyInUseError("API key is already in use, please close previous client.");
        }
        T openWeatherClient = clientSupplier.get();
        registry.put(apiKey, openWeatherClient);
        return openWeatherClient;
    }

    static synchronized void unregisterClient(String apiKey) {
        registry.remove(apiKey);
    }
}