package org.example.interpark.domain.concert.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.repository.ConcertCacheRepository;
import org.example.interpark.domain.concert.repository.ConcertRepositoryV1;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.example.interpark.util.PageQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertCacheService {

  private final ConcertRepositoryV1 concertRepositoryV1;
  private final SearchKeywordService searchKeywordService;
  private final ConcertCacheRepository concertCacheRepository;
  private final ConcertPopularSearchService concertPopularSearchService;


  public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcerts(String keyword,
      PageQuery pageQuery) {
    searchKeywordService.saveSearchKeyword(keyword);

    Page<ConcertSearchResponseDto> concertList = concertRepositoryV1.searchConcerts(keyword,
            pageQuery.toPageable())
        .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
            concert.getTotalAmount(), concert.getAvailableAmount()));

    return org.example.interpark.util.Page.from(concertList);
  }

  @Cacheable(value = "concerts", key = "#keyword")
  public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsByCache(
      String keyword, PageQuery pageQuery) {

    searchKeywordService.saveSearchKeyword(keyword);

    Page<ConcertSearchResponseDto> concertList = concertRepositoryV1.searchConcerts(keyword,
            pageQuery.toPageable())
        .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
            concert.getTotalAmount(), concert.getAvailableAmount()));

    return org.example.interpark.util.Page.from(concertList);
  }

  // Hash 기반 검색 기능
  public List<ConcertSearchResponseDto> searchConcertsByHash(String keyword, PageQuery pageQuery) {
    if (keyword != null && !keyword.isBlank()) {
      concertPopularSearchService.incrementSearchCount(keyword); // 인기 검색어 저장
    }

    // Redis 에서 먼저 데이터 조회
    List<ConcertSearchResponseDto> cachedConcerts = concertCacheRepository.getConcertsByKeyword(keyword);
    if (cachedConcerts != null && !cachedConcerts.isEmpty()) {
      System.out.println("Redis 캐시에서 검색 결과 반환: " + keyword);
      return cachedConcerts;
    }

    System.out.println("Redis 캐시에 없음 → 쿼리문 실행: " + keyword);

    // 캐시에 없으면 DB에서 검색 후 Redis에 저장
    Page<ConcertSearchResponseDto> concertList = concertRepositoryV1.searchConcerts(keyword, pageQuery.toPageable())
        .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
            concert.getTotalAmount(), concert.getAvailableAmount()));

    List<ConcertSearchResponseDto> resultList = concertList.getContent();
    concertCacheRepository.saveConcerts(keyword, resultList);  // Redis에 저장

    return resultList;
  }

}
