package kameleoon.weathersdkstarter.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

public record InMemoryCacheProps(@NotNull @Positive Long maxCacheRecords, @NotNull Duration cacheRecordTtl) {
}