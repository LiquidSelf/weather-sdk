package kameleoon.weathersdk.internal.poller;

import kameleoon.weathersdk.cache.CacheManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class CachePoller<T> {

    private final Duration pollInterval;
    private final Duration pollTimeout;
    private final CacheManager<T> cacheManager;
    private final ScheduledExecutorService scheduler;

    private final Function<String, CompletableFuture<T>> fetchFunction;

    public CachePoller(@NonNull Duration pollInterval,
                       @NonNull Duration pollTimeout,
                       @NonNull CacheManager<T> cacheManager,
                       @NonNull Function<String, CompletableFuture<T>> fetchFunction
    ) {
        this.pollInterval = pollInterval;
        this.pollTimeout = pollTimeout;
        this.cacheManager = cacheManager;
        this.fetchFunction = fetchFunction;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("cache-poller");
            return t;
        });
    }

    private void updateCache() {
        try {
            List<CompletableFuture<T>> futures = cacheManager.keySet().stream()
                    .map(key -> CompletableFuture.supplyAsync(() -> {
                        try {
                            T value = fetchFunction.apply(key).join(); // join безопасно ждёт
                            cacheManager.put(key, value);
                            return value;
                        } catch (Exception ex) {
                            log.error("Failed to update city: {}", key, ex);
                            return null;
                        }
                    }))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(pollTimeout.toMillis(), TimeUnit.MILLISECONDS)
                    .exceptionally(e -> {
                        log.error("Failed to update cities", e);
                        return null;
                    })
                    .join();
        } catch (Throwable t) {
            log.error("Unexpected error in cache poller", t);
        }
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
                this::updateCache,
                0,
                this.pollInterval.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
