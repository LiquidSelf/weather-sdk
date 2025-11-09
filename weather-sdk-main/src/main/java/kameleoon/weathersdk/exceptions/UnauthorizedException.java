package kameleoon.weathersdk.exceptions;

/**
 * Indicates wrong credentials on request
 */
public class UnauthorizedException extends ApiClientException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
