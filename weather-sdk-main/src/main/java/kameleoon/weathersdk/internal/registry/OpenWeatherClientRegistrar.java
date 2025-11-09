package kameleoon.weathersdk.internal.registry;

import kameleoon.weathersdk.exceptions.ApiKeyAlreadyInUseError;
import kameleoon.weathersdk.internal.Internal;
import kameleoon.weathersdk.internal.httpclients.openweather.WebApiClient;

import java.util.function.Supplier;

@Internal
public interface OpenWeatherClientRegistrar<T extends WebApiClient> {
    default T tryRegisterClient(String apiKey, Supplier<T> clientSupplier) throws ApiKeyAlreadyInUseError {
        return OpenWeatherClientRegistry.tryRegisterClient(apiKey, clientSupplier);
    }

    default void unregisterClient(String apiKey) {
        OpenWeatherClientRegistry.unregisterClient(apiKey);
    }
}
