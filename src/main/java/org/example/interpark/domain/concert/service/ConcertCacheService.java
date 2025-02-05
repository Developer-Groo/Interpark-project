package org.example.interpark.domain.concert.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.repository.ConcertCacheRepository;
import org.example.interpark.domain.concert.repository.ConcertQueryRepository;
import org.example.interpark.util.PageQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertCacheService {

  private final ConcertQueryRepository concertQueryRepository;
  private final ConcertCacheRepository concertCacheRepository;
  private final RedisTemplate<String, Object> redisTemplateV3;

  /**
   * Local Memory Cache 적용
   */
  @Cacheable(value = "inMemoryCache", key = "#keyword", cacheManager = "localCacheManager")
  public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsByInMemoryCache(
      String keyword, PageQuery pageQuery) {
    log.info("InMemoryCache 검색 호출: {}", keyword);
    Page<ConcertSearchResponseDto> concertList = concertQueryRepository.searchConcerts(keyword,
            pageQuery.toPageable())
        .map(ConcertSearchResponseDto::from);

    return org.example.interpark.util.Page.from(concertList);
  }

  /**
   * Redis Cache 적용
   */
  @Cacheable(value = "concerts", key = "#keyword", cacheManager = "redisCacheManager")
  public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsByRedisCache(
      String keyword, PageQuery pageQuery) {
    Page<ConcertSearchResponseDto> concertList = concertQueryRepository.searchConcerts(keyword,
            pageQuery.toPageable())
        .map(ConcertSearchResponseDto::from);

    // 검색 결과에서 ID-검색어 매핑 저장 (String 기반 캐싱에서도 ID 추적 가능하게)
    saveKeywordMapping(keyword, concertList.getContent());

    return org.example.interpark.util.Page.from(concertList);
  }

  /**
   *  검색된 ID를 검색어와 매핑하여 저장
   */
  private void saveKeywordMapping(String keyword, List<ConcertSearchResponseDto> concerts) {
    for (ConcertSearchResponseDto concert : concerts) {
      String keywordCacheKey = "concerts:concertId:" + concert.id();
      redisTemplateV3.opsForSet().add(keywordCacheKey, keyword);
      redisTemplateV3.expire(keywordCacheKey, 10, TimeUnit.MINUTES);
    }
  }

  /**
   * Redis Hash 기반 검색
   */
  @Transactional
  public List<ConcertSearchResponseDto> searchConcertsByHash(String keyword,
      PageQuery pageQuery) {
    // Redis 캐시에서 데이터 조회
    List<ConcertSearchResponseDto> cachedConcerts = getCachedConcerts(keyword);
    if (!cachedConcerts.isEmpty()) {
      return cachedConcerts;
    }

    // 캐시에 없으면 DB 에서 검색
    List<ConcertSearchResponseDto> concertList = fetchConcerts(keyword, pageQuery);

    // 검색 결과를 Redis 에 저장
    saveConcertsByRedis(keyword, concertList);

    return concertList;
  }

  /**
   * 캐싱된 검색 결과 조회
   */
  private List<ConcertSearchResponseDto> getCachedConcerts(String keyword) {
    List<ConcertSearchResponseDto> cachedConcerts = concertCacheRepository.getConcertsByKeyword(
        keyword);
    if (!cachedConcerts.isEmpty()) {
      log.info("Redis 캐시에서 검색 결과 반환: {}", keyword);
    }
    return cachedConcerts;
  }

  /**
   * MySQL 에서 검색
   */
  private List<ConcertSearchResponseDto> fetchConcerts(String keyword, PageQuery pageQuery) {
    log.info("Redis 캐시에 없음 → 쿼리문 실행: {}", keyword);
    return concertQueryRepository.searchConcerts(keyword, pageQuery.toPageable())
        .map(ConcertSearchResponseDto::from)
        .toList();
  }

  /**
   * 검색 결과를 Redis 에 저장
   */
  public void saveConcertsByRedis(String keyword, List<ConcertSearchResponseDto> concerts) {
    if (!concerts.isEmpty()) {
      concertCacheRepository.saveConcerts(keyword, concerts);
      log.info("검색 결과를 Redis 에 저장: {}", keyword);
    }
  }

  /**
   *  캐시 삭제
   */
  public void evictConcertCacheById(Integer concertId) {
    log.info("콘서트 ID({}) 기반 검색 캐시 삭제", concertId);
    concertCacheRepository.deleteConcertCacheById(concertId);
  }

//  public void evictHashCacheById(Integer concertId) {
//    log.info("콘서트 ID({}) 기반 검색 캐시 삭제", concertId);
//    concertCacheRepository.deleteHashCacheById(concertId);
//  }
}


