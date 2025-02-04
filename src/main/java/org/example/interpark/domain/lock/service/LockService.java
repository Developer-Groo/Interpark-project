package org.example.interpark.domain.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.lock.repository.RedisLockRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {
    private final RedisLockRepository redisLockRepository;

    public <T> CompletableFuture<T> withLock(int key, Supplier<CompletableFuture<T>> logic) {
        return redisLockRepository.lock(key)
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
}