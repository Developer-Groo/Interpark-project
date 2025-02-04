package org.example.interpark.domain.lock.repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

//@Repository
//@RequiredArgsConstructor
//public class RedisLockRepository {
//
//    private final RedisAsyncCommands<String, String> asyncCommands;
//
//    private String generateLockKey(int key) {
//        return "lock:" + key;
//    }
//
//    public CompletableFuture<Boolean> lock(int key) {
//        String lockKey = generateLockKey(key);
//        return asyncCommands
//                .set(lockKey, "LOCKED", SetArgs.Builder.nx().px(3000))
//                .toCompletableFuture()
//                .thenApply("OK"::equals);
//    }
//
//    public CompletableFuture<Boolean> unlock(int key) {
//        String lockKey = generateLockKey(key);
//        return asyncCommands.del(lockKey)
//                .toCompletableFuture()
//                .thenApply(deletedCount -> deletedCount > 0);
//    }
//}

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisLockRepository {

    private final RedissonClient redissonClient;

    private String generateLockKey(int key) {
        return "lock:" + key;
    }

    public CompletableFuture<Boolean> lock(int key) {
        String lockKey = generateLockKey(key);
        RLock lock = redissonClient.getLock(lockKey);
        RFuture future = lock.tryLockAsync(10, 3, TimeUnit.SECONDS);
//        future.thenRun(()->log.info("lock 획득::::::::" + Thread.currentThread().getId()));
        return future.toCompletableFuture();
    }

    public CompletableFuture<Boolean> unlock(int key) {
        String lockKey = generateLockKey(key);
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlockAsync().toCompletableFuture().thenApply(ignored -> true);
            log.info("unlock 시도::::::::" + Thread.currentThread().getId());
            return CompletableFuture.completedFuture(true);
        }
        log.info("lock이 없음:::::::::::" + Thread.currentThread().getId());
        return CompletableFuture.completedFuture(false);
    }
}