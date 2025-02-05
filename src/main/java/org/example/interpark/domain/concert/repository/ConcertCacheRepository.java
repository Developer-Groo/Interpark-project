package org.example.interpark.domain.concert.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
@Slf4j
@Repository
@RequiredArgsConstructor
public class ConcertCacheRepository {

    private static final String CACHE_PREFIX = "concerts:";
    private static final String KEYWORDS_PREFIX = "concerts:concertId:";

    private final HashOperations<String, String, ConcertSearchResponseDto> hashOperations;
    private final RedisTemplate<String, Object> redisTemplate;

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

        // TTL 설정 (10분)
        redisTemplate.expire(cacheKey, 10, TimeUnit.MINUTES);

        // 콘서트 ID 기준으로 검색어 저장 (ID → [검색어 리스트] 맵핑)
        for (ConcertSearchResponseDto concert : concerts) {
            String keywordCacheKey = KEYWORDS_PREFIX + concert.id();
            redisTemplate.opsForSet().add(keywordCacheKey, keyword);
            redisTemplate.expire(keywordCacheKey, 10, TimeUnit.MINUTES);
        }
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
        redisTemplate.delete(CACHE_PREFIX + keyword);
    }

    /**
     * 특정 콘서트 ID가 포함된 모든 검색어의 캐시 삭제
     */
    public void deleteConcertCacheById(Integer concertId) {
        String keywordCacheKey = KEYWORDS_PREFIX + concertId;

        // 해당 ID가 포함된 모든 검색어 가져오기
        Set<Object> keywords = redisTemplate.opsForSet().members(keywordCacheKey);
        if (keywords != null) {
            for (Object keyword : keywords) {
                deleteConcertCache(keyword.toString());
            }
        }

        // 검색어 목록 캐시도 삭제
        redisTemplate.delete(keywordCacheKey);
    }

//    public void deleteHashCacheById(Integer concertId) {
//        // 모든 `concerts:*` 키 조회
//        Set<String> cacheKeys = redisTemplate.keys("concerts:*");
//
//        if (cacheKeys != null) {
//            for (String cacheKey : cacheKeys) {
//                // 해당 캐시에 `concertId`가 포함되어 있는지 확인
//                Boolean exists = redisTemplate.opsForHash().hasKey(cacheKey, concertId.toString());
//
//                if (Boolean.TRUE.equals(exists)) {
//                    // 해당 캐시 삭제
//                    redisTemplate.delete(cacheKey);
//                    log.info("Hash 캐시 삭제 완료: {}", cacheKey);
//                }
//            }
//        }
//    }
}
