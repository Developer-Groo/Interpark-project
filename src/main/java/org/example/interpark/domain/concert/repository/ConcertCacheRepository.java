package org.example.interpark.domain.concert.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertCacheRepository {

  private static final String CACHE_PREFIX = "concerts:";
  private final HashOperations<String, String, ConcertSearchResponseDto> hashOperations;
  private final RedisTemplate<String, Object> redisTemplate;

  // Redis에 검색 결과 저장
  public void saveConcerts(String keyword, List<ConcertSearchResponseDto> concerts) {
    String cacheKey = CACHE_PREFIX + keyword;

    // 데이터 저장
    for (ConcertSearchResponseDto concert : concerts) {
      hashOperations.put(cacheKey, String.valueOf(concert.id()), concert);
    }

    // TTL 설정 (10분)
    redisTemplate.expire(cacheKey, 10, TimeUnit.MINUTES);
  }

  // Redis 에서 검색 결과 조회
  public List<ConcertSearchResponseDto> getConcertsByKeyword(String keyword) {
    String cacheKey = CACHE_PREFIX + keyword;

    // 데이터 조회
    Map<String, ConcertSearchResponseDto> cachedConcerts = hashOperations.entries(cacheKey);

    if (cachedConcerts == null || cachedConcerts.isEmpty()) {
      return null;  // 캐시 데이터가 없으면 null
    }

    return cachedConcerts.values().stream().collect(Collectors.toList());
  }

  // 특정 키워드의 캐시 삭제
  public void deleteConcertCache(String keyword) {
    redisTemplate.delete(CACHE_PREFIX + keyword);
  }

}
