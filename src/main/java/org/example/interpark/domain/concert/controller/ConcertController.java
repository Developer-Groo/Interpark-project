package org.example.interpark.domain.concert.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.service.ConcertService;
import org.example.interpark.domain.search.service.RedisSearchKeywordService;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {

    private final ConcertService concertService;
    private final SearchKeywordService searchKeywordService;
    private final RedisSearchKeywordService redisSearchKeywordService;

    /**
     * Cache 를 적용하지 않은 API
     */
    @GetMapping("/v1/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcerts(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        log.info("일단 들어옴");
        searchKeywordService.saveSearchKeyword(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertService.searchConcerts(keyword, pageQuery));
    }

    /**
     * Local Memory Cache 를 적용한 API
     */
    @GetMapping("/v2/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcertsByCache(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        searchKeywordService.saveSearchKeyword(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertService.searchConcertsByCache(keyword, pageQuery));
    }

    /**
     * Redis Cache 를 적용한 API
     */
    @GetMapping("/v2/redis/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcertsByRedis(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        redisSearchKeywordService.incrementSearchCount(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertService.searchConcertsByCache(keyword, pageQuery));
    }
}