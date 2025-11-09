package kameleoon.weathersdk.builders;

public enum Mode {

    /**
     * Disable caching
     */
    NO_CACHE,

    /**
     * Cache-aside cacheing
     */
    REQUEST_CACHE,

    /**
     * Cache being updated in background
     */
    POLLING_CACHE
}
