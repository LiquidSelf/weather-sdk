package kameleoon.weathersdk.builders;

/**
 * Cache operation modes.
 */
public enum Mode {

    /** Disable caching completely. */
    NO_CACHE,

    /** Cache-aside: read-through on demand. */
    REQUEST_CACHE,

    /** Cache updated in background via polling. */
    POLLING_CACHE
}