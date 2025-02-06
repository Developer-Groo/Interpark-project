package org.example.interpark.domain.concert.repository;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertCacheRepository {

    private static final String CACHE_PREFIX = "concerts:";
    private final HashOperations<String, String, ConcertSearchResponseDto> hashOperations;
    private final RedisTemplate<String, Object> redisTemplateByHash;

    /**
     * Redis 에 검색 결과 저장
     */
    public void saveConcerts(String keyword, List<ConcertSearchResponseDto> concerts) {
        if (concerts.isEmpty()) {
            return;
        }

        String cacheKey = CACHE_PREFIX + keyword;

        Map<String, ConcertSearchResponseDto> concertMap = concerts.stream()
            .collect(
                Collectors.toMap(
                    dto -> String.valueOf(dto.id()),
                    dto -> dto
                )
            );

        hashOperations.putAll(cacheKey, concertMap);

        // TTL 설정 (2분)
        redisTemplateByHash.expire(cacheKey, 2, TimeUnit.MINUTES);
    }

    /**
     * Redis 에서 검색 결과 조회
     */
    public List<ConcertSearchResponseDto> getConcertsByKeyword(String keyword) {
        String cacheKey = CACHE_PREFIX + keyword;

        // 데이터 조회
        Map<String, ConcertSearchResponseDto> cachedConcerts = hashOperations.entries(cacheKey);

        return new ArrayList<>(cachedConcerts.isEmpty() ? Collections.emptyList() : cachedConcerts.values());
    }

    /**
     * 특정 키워드의 캐시 삭제
     */
    public void deleteConcertCache(String keyword) {
        redisTemplateByHash.delete(CACHE_PREFIX + keyword);
    }

}
