package kameleoon.weathersdk.exceptions;

/**
 * Base client exception
 */
public class ApiClientException extends RuntimeException {
    public ApiClientException(String err) {
        super(err);
    }
    public ApiClientException(String err, Throwable cause) {
        super(err, cause);
    }
}
