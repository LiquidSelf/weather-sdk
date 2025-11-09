# Weather SDK - core module

----------------------------------------
This SDK provides an easy way to get weather data from OpenWeather API. It is designed as part of a test assignment for Kameleoon.

The SDK provides two types of clients (two main interfaces):
- WeatherApiStandardClient — a blocking and CompletableFuture-based client
- WeatherApiReactiveClient — a reactive, non-blocking client based on Project Reactor

Each client allows fetching current weather information for a given city. It's only possible to create 1 client per API key, to release API key use client.close() method.

----------------------------------------
## Standard client usage example: 
```java
WeatherApiStandardClient standardClient = WeatherApiStandardClient.builder()
                .mode(Mode.POLLING_CACHE)
                .baseUrl("https://api.openweathermap.org/data/2.5/weather")
                .apiKey("your api key")
                .responseTimeout(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMinutes(5))
                .pollTimeout(Duration.ofSeconds(5))
                .inMemoryCache(InMemoryCacheConfig.of(10L, Duration.ofSeconds(10)))
                .build();

WeatherInfo parisWeather = standardClient.fetchWeather(CityName.like("Paris"));
WeatherInfo tokyoWeather = standardClient.fetchWeatherAsync(CityName.like("Tokyo")).join();

standardClient.close();
```
----------------------------------------
## Reactive client usage example:
```java
 WeatherApiReactiveClient reactiveClient = WeatherApiReactiveClient.builder()
                .mode(Mode.REQUEST_CACHE)
                .baseUrl("https://api.openweathermap.org/data/2.5/weather")
                .apiKey("your api key")
                .responseTimeout(Duration.ofSeconds(5))
                .inMemoryCache(InMemoryCacheConfig.of(10L, Duration.ofSeconds(10)))
                .build();

WeatherInfo londonWeather = reactiveClient.fetchWeather(CityName.like("London")).block();

reactiveClient.close();
```
----------------------------------------

## Or just preconfigured:

```java
WeatherApiStandardClient simpleClient = WeatherClients.createSimpleClient("api_key", "https://api.openweathermap.org/data/2.5/weather");
WeatherInfo londonWeather = simpleClient.fetchWeather(CityName.like("Moscow"));

simpleClient.close();
```
----------------------------------------

## Mode

----------------------------------------
- Mode.NO_CACHE — disables caching completely
- Mode.REQUEST_CACHE — enables request-level caching
- Mode.POLLING_CACHE — periodically refreshes cache in the background
----------------------------------------