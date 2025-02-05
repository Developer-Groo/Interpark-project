package org.example.interpark.domain.concert.service;

import java.util.HashSet;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertCacheScheduler {

    private final RedisTemplate<String, String> redisTemplate;

    //   @Scheduled(cron = "0 */1 * * * *")
    @Scheduled(cron = "0 0 12 * * ?")
    public void clearCacheAtMidnight() {
        HashSet<String> keysToDelete = new HashSet<>();

        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory())
            .getConnection();

        ScanOptions options = ScanOptions.scanOptions()
            .match("concerts*")
            .count(100)
            .build();

        connection.keyCommands().scan(options).forEachRemaining(key ->
            keysToDelete.add(new String(key))
        );

        log.info("삭제할 키 목록 {}", keysToDelete);

        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
            log.info("모든 검색 캐시 데이터 삭제");
        }
    }
}
