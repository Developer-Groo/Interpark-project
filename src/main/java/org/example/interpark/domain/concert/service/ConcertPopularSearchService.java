package org.example.interpark.domain.concert.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertPopularSearchService {

  @Qualifier("redisTemplateV2")
  private final RedisTemplate<String, String> redisTemplateV2;
  private static final String POPULAR_SEARCH_KEY = "popular_keywords"; // ZSET Key

  public void incrementSearchCount(String keyword) {
    System.out.println("인기 검색어 증가: " + keyword);
    redisTemplateV2.opsForZSet().incrementScore(POPULAR_SEARCH_KEY, keyword, 1);
  }

  public List<String> getTopSearchKeywords() {
    System.out.println("인기 검색어 조회 요청");
    return redisTemplateV2.opsForZSet().reverseRange(POPULAR_SEARCH_KEY, 0, 9)
        .stream().toList();
  }

}
