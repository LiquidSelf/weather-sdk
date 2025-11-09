package kameleoon.weathersdk.cache.inmemory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class InMemoryCacheConfig {
    private final Long maxCacheRecords;
    private final Duration cacheRecordTTL;

    public InMemoryCacheConfig withMaxCacheRecords(Long maxCacheRecords) {
        return new InMemoryCacheConfig(maxCacheRecords, this.cacheRecordTTL);
    }

    public InMemoryCacheConfig withCacheRecordTTL(Duration cacheRecordTTL) {
        return new InMemoryCacheConfig(this.maxCacheRecords, cacheRecordTTL);
    }

    public static InMemoryCacheConfig of(Long maxCacheRecords, Duration cacheRecordTTL) {
        return new InMemoryCacheConfig(maxCacheRecords, cacheRecordTTL);
    }
}