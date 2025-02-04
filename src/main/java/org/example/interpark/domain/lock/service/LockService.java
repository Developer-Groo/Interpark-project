package org.example.interpark.domain.lock.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.lock.repository.RedisLockRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private static final int MAX_RETRY = 2;
    private static final long RETRY_DELAY_MS = 100;
    private final RedisLockRepository redisLockRepository;

    public <T> CompletableFuture<T> withLock(int key, Supplier<CompletableFuture<T>> logic) {
        return attemptLock(key, 1).thenCompose(acquired -> {
            if (!acquired) {
                return CompletableFuture.failedFuture(
                    new IllegalStateException("Cannot get the key: " + key));
            }

            return logic.get().whenComplete((result, error) -> {
                redisLockRepository.unlock(key).exceptionally(unlockError -> {
                    log.error("Unlock failed for key {}: {}", key, unlockError.getMessage());
                    return null;
                });
            });
        });
    }

    private CompletableFuture<Boolean> attemptLock(int key, int attempt) {
        return redisLockRepository.lock(key).thenCompose(success -> {
            if (success) {
                return CompletableFuture.completedFuture(true);
            } else if (attempt < MAX_RETRY) {

                return CompletableFuture.supplyAsync(() -> null,
                        CompletableFuture.delayedExecutor(RETRY_DELAY_MS, TimeUnit.MILLISECONDS))
                    .thenCompose(ignored -> {
                        return attemptLock(key, attempt + 1);
                    });
            } else {
                log.error("Failed to acquire lock for key {} after {} attempts", key, MAX_RETRY);
                return CompletableFuture.completedFuture(false);
            }
        });
    }
}

//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class LockService {
//
//    private final RedisLockRepository redisLockRepository;
//    private static final int MAX_RETRY = 10;
//    private static final long RETRY_DELAY_MS = 100;
//
//    public <T> CompletableFuture<T> withLock(int key, Supplier<CompletableFuture<T>> logic) {
//        return redisLockRepository.lock(key)
//            .thenCompose(acquired -> {
//                if (!acquired) {
//                    return CompletableFuture.failedFuture(new IllegalStateException("Cannot get the key: " + key));}
//
//                return logic.get()
//                    .whenComplete((result, error) -> {
//                        log.info("unlock 시도::::::::" + Thread.currentThread().getId());
//                        redisLockRepository.unlock(key)
//                            .exceptionally(unlockError -> {
//                                log.error("Unlock failed for key {}: {}", key, unlockError.getMessage());
//                                return null;
//                            });
//                    });
//            });
//    }
//
//}
