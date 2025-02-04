package org.example.interpark.domain.lock.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedissonClient redissonClient;

    private String generateLockKey(int key) {
        return "lock:" + key;
    }

    public CompletableFuture<Boolean> lock(int key) {
        String lockKey = generateLockKey(key);
        RLock lock = redissonClient.getLock(lockKey);

        return CompletableFuture.supplyAsync(() -> {
            try{
                return lock.tryLock(5, 10, TimeUnit.SECONDS);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> unlock(int key) {
        String lockKey = generateLockKey(key);
        RLock lock = redissonClient.getLock(lockKey);

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    log.info("UNLOCK this lock: {}", Thread.currentThread().getId());
                    lock.unlock();
                    return true;
                }
            } catch (IllegalMonitorStateException e) {
                return false;
            }
            return false;
        });
    }
}
