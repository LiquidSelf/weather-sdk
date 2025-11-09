# Weather SDK - spring boot starter module

----------------------------------------
This module allows quick start and easy configuration with spring boot.

# Hot to use:

### 1. Import

```xml

<dependency>
    <groupId>kameleoon</groupId>
    <artifactId>weather-sdk-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configure

```yaml
openweather:
  client:
    reactive: false
    mode: POLLING_CACHE
    base-url: "open_weather_base_url"
    api-key: "my_api_key"
    response-timeout: 5s
    poll-interval: 60s
    poll-timeout: 10s
    in-memory-cache:
      max-cache-records: 10
      cache-record-ttl: 10m
```

### 3. Inject respective bean type: WeatherApiStandardClient for standard client, and WeatherApiReactiveClient for reactive.

# Configuration Properties (`openweather.client`)

| Property                            | Type       | Required                                                                    | Default  | Description                                                                                     |
|-------------------------------------|------------|-----------------------------------------------------------------------------|----------|-------------------------------------------------------------------------------------------------|
| `mode`                              | `Mode`     | No                                                                          | NO_CACHE | Mode of client, one of NO_CACHE, REQUEST_CACHE, POLLING_CACHE                                   |
| `reactive`                          | `boolean`  | No                                                                          | `false`  | Enables the reactive client                                                                     |
| `base-url`                          | `String`   | Yes                                                                         | —        | Base URL of the OpenWeather API. e.g https://api.openweathermap.org/data/2.5/weather            |
| `api-key`                           | `String`   | Yes                                                                         | —        | API key used to authenticate requests to OpenWeather.                                           |
| `response-timeout`                  | `Duration` | No                                                                          | `30s`    | Maximum allowed time to wait for a response from the OpenWeather API.                           |
| `in-memory-cache.max-cache-records` | `Long`     | Only for REQUEST_CACHE and POLLING_CACHE mode. (if no other cache provided) | —        | Maximum number of cached weather records stored in memory.                                      |
| `in-memory-cache.cache-record-ttl`  | `Duration` | Only for REQUEST_CACHE and POLLING_CACHE mode. (if no other cache provided) | —        | Time-to-live for cached records before expiration.                                              |
| `poll-interval`                     | `Duration` | POLLING_CACHE mode only                                                     | —        | Interval between polling requests when using a polling mode. (Used in POLLING_CACHE Mode only.) |
| `poll-timeout`                      | `Duration` | POLLING_CACHE mode only                                                     | —        | Maximum time to keep polling before giving up. (Used in POLLING_CACHE Mode only.)               |
