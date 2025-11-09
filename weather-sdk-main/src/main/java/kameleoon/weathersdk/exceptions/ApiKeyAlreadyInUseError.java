package kameleoon.weathersdk.exceptions;

/**
 * Thrown in case is API key is already used by some other client, try to release it (close).
 */
public class ApiKeyAlreadyInUseError extends RuntimeException {
    public ApiKeyAlreadyInUseError(String message) {
        super(message);
    }
}
