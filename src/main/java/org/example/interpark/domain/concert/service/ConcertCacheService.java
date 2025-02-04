package org.example.interpark.domain.concert.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.repository.ConcertCacheRepository;
import org.example.interpark.domain.concert.repository.ConcertQueryRepository;
import org.example.interpark.util.PageQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertCacheService {

    private final ConcertQueryRepository concertQueryRepository;
    private final ConcertCacheRepository concertCacheRepository;

    /**
     * Local Memory Cache 적용
     */
    @Cacheable(value = "inMemoryCache", key = "#keyword", cacheManager = "localCacheManager")
    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsByInMemoryCache(
        String keyword, PageQuery pageQuery) {
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

        return org.example.interpark.util.Page.from(concertList);
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

}
