package org.example.interpark.domain.lock.repository;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisAsyncCommands<String, String> asyncCommands;

    private String generateLockKey(int key) {
        return "lock:" + key;
    }

    public CompletableFuture<Boolean> lock(int key) {
        String lockKey = generateLockKey(key);
        return asyncCommands
                .set(lockKey, "LOCKED", SetArgs.Builder.nx().px(3000))
                .toCompletableFuture()
                .thenApply("OK"::equals);
    }

    public CompletableFuture<Boolean> unlock(int key) {
        String lockKey = generateLockKey(key);
        return asyncCommands.del(lockKey)
                .toCompletableFuture()
                .thenApply(deletedCount -> deletedCount > 0);
    }
}
