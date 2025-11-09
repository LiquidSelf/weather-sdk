package kameleoon.weathersdk.internal.poller;

import kameleoon.weathersdk.cache.ReactiveCacheManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Slf4j
public class ReactiveCachePoller<T> {

    private final Duration pollInterval;
    private final Duration pollTimeout;
    private final ReactiveCacheManager<T> cacheManager;
    private Disposable pollingTask;

    private final Function<String, Mono<T>> fetchFunction;

    public ReactiveCachePoller(@NonNull Duration pollInterval,
                               @NonNull Duration pollTimeout,
                               @NonNull ReactiveCacheManager<T> cacheManager,
                               @NonNull Function<String, Mono<T>> fetchFunction
    ) {
        this.pollInterval = pollInterval;
        this.pollTimeout = pollTimeout;
        this.cacheManager = cacheManager;
        this.fetchFunction = fetchFunction;
    }

    private Mono<Void> updateCache() {
        return cacheManager.keySet()
                .flatMapMany(Flux::fromIterable)
                .flatMap(key -> fetchFunction.apply(key)
                        .flatMap(value -> cacheManager.put(key, value))
                        .onErrorResume(e -> {
                            log.warn("Failed to update cache fo{}: {}", key, e.getMessage());
                            return Mono.empty();
                        })
                )
                .timeout(pollTimeout)
                .onErrorResume(TimeoutException.class, e -> {
                    log.warn("Timeout while updating cities");
                    return Mono.empty();
                })
                .then();
    }

    public void start() {
        this.pollingTask = Flux.interval(this.pollInterval)
                .concatMap(tick -> updateCache())
                .onErrorContinue((e, o) -> log.warn("Error during polling: {}", e.getMessage()))
                .onBackpressureDrop()
                .subscribe();
    }

    public void shutdown() {
        pollingTask.dispose();
    }
}
