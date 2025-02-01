package org.example.interpark.domain.ticket.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisTicketLockRepository {
    /*
    Redis 분산 락 구현 용 Repository
    1. Lock 용 키 생성 concertId:1 형태
    2. Lock 키가 존재하면 접근 불가 +  (획득 시도 텀 Thread.sleep , 획득 시도 횟수 제한)
    3. 데이터 얻은 후 unlock : lock 용 키 삭제
     */

    private final RedisTemplate<String, String> redisTemplate;


    /*
    setIfAbsent : 키가 존재하지 않으면 값 설정, 유효시간(락 점유 최대 시간)
    true -> 생성 후 접근 / false -> 키가 이미 있음
     */
    public Boolean lock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofMillis(3000));
    }


    public Boolean unlock(Object key) {
        return redisTemplate.delete(key.toString());
    }

}
