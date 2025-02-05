package org.example.interpark.domain.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSearchKeywordService {

    private final RedisTemplate<String, String> redisTemplateV2;
    private static final String POPULAR_SEARCH_KEY = "popular_keywords"; // ZSet Key

    public void incrementSearchCount(String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            log.info("인기 검색어 증가: {}", keyword);
            redisTemplateV2.opsForZSet().incrementScore(POPULAR_SEARCH_KEY, keyword, 1);
        }
    }

    public List<String> getTopSearchKeywords() {
        log.info("인기 검색어 조회 요청");
        Set<String> reverseRange = redisTemplateV2.opsForZSet()
            .reverseRange(POPULAR_SEARCH_KEY, 0, 9);
        return new ArrayList<>(reverseRange != null ? reverseRange : Collections.emptySet());
    }
}
