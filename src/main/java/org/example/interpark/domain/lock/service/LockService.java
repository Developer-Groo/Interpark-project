package org.example.interpark.domain.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.lock.repository.RedisLockRepository;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {
    private final RedisLockRepository redisLockRepository;

    public <T> T withLock(int key, Supplier<T> logic) {
        if (!redisLockRepository.lock(key)) {
            throw new RuntimeException("Failed to acquire lock for key: " + key + "::::" + Thread.currentThread().getId());
        }

        try {
            return logic.get();
        } catch (Exception e) {
            log.error(" ::::" + Thread.currentThread().getId(), e);
            throw new RuntimeException(e);
        } finally {
            redisLockRepository.unlock(key);
        }
    }
}