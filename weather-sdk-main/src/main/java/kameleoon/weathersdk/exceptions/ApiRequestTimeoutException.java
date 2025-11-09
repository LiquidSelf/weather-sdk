package kameleoon.weathersdk.exceptions;

/**
 * Response timed out
 */
public class ApiRequestTimeoutException extends ApiClientException {
    public ApiRequestTimeoutException(String err) {
        super(err);
    }

    public ApiRequestTimeoutException(String err, Throwable cause) {
        super(err, cause);
    }
}