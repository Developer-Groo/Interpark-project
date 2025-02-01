package org.example.interpark.domain.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.lock.repository.RedisLockRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private final RedisLockRepository redisLockRepository;
    private static final int MAX_RETRY = 10;
    private static final long RETRY_DELAY_MS = 100; // 재시도 사이의 지연 시간

    public <T> CompletableFuture<T> withLock(int key, Supplier<CompletableFuture<T>> logic) {
        return attemptLock(key, 1)
                .thenCompose(acquired -> {
                    if (!acquired) {
                        return CompletableFuture.failedFuture(new IllegalStateException("Cannot get the key: " + key));}

                    return logic.get()
                            .whenComplete((result, error) -> {
                                redisLockRepository.unlock(key)
                                        .exceptionally(unlockError -> {
                                            log.error("Unlock failed for key {}: {}", key, unlockError.getMessage());
                                            return null;
                                        });
                            });
                });
    }

    private CompletableFuture<Boolean> attemptLock(int key, int attempt) {
        return redisLockRepository.lock(key)
                .thenCompose(success -> {
                    if (success) {
                        return CompletableFuture.completedFuture(true);
                    } else if (attempt < MAX_RETRY) {

                        return CompletableFuture
                                .supplyAsync(
                                        () -> null,
                                        CompletableFuture.delayedExecutor(RETRY_DELAY_MS, TimeUnit.MILLISECONDS))
                                .thenCompose(ignored -> {
                                    log.info("Lock retry {}/{} for key {}", attempt, MAX_RETRY, key);
                                    return attemptLock(key, attempt + 1);
                                });
                    } else {
                        log.error("Failed to acquire lock for key {} after {} attempts", key, MAX_RETRY);
                        return CompletableFuture.completedFuture(false);
                    }
                });
    }
}