package org.example.interpark.domain.concert.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.dto.request.ConcertCreateRequestDto;
import org.example.interpark.domain.concert.dto.request.ConcertUpdateRequestDto;
import org.example.interpark.domain.concert.dto.response.ConcertResponseDto;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.service.ConcertCacheService;
import org.example.interpark.domain.concert.service.ConcertService;
import org.example.interpark.domain.search.service.RedisSearchKeywordService;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final ConcertCacheService concertCacheService;
    private final RedisSearchKeywordService redisSearchKeywordService;

    /**
     * Cache 를 적용하지 않은 검색 API
     */
    @GetMapping("/v1/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcerts(
        @RequestParam(required = false) String keyword, PageQuery pageQuery) {
        searchKeywordService.saveSearchKeyword(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertService.searchConcerts(keyword, pageQuery));
    }

    /**
     * Local Memory Cache 를 적용한 검색 API
     */
    @GetMapping("/v2/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcertsByCache(
        @RequestParam(required = false) String keyword, PageQuery pageQuery) {
        searchKeywordService.saveSearchKeyword(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertCacheService.searchConcertsByCache(keyword, pageQuery));
    }

    /**
     * Redis Cache 를 적용한 검색 API
     */
    @GetMapping("/v2/redis/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcertsByRedis(
        @RequestParam(required = false) String keyword, PageQuery pageQuery) {
        redisSearchKeywordService.incrementSearchCount(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertCacheService.searchConcertsByCache(keyword, pageQuery));
    }

    /**
     * Hash 기반 검색 API
     */
    @GetMapping("/v3/concerts")
    public ResponseEntity<List<ConcertSearchResponseDto>> getConcertsFromCache(
        @RequestParam(required = false) String keyword, PageQuery pageQuery) {
        redisSearchKeywordService.incrementSearchCount(keyword);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertCacheService.searchConcertsByHash(keyword, pageQuery));
    }

    /**
     * 콘서트 생성
     */
    @PostMapping("/concerts")
    public ResponseEntity<ConcertResponseDto> createConcert(
        @RequestBody ConcertCreateRequestDto request) {
        return ResponseEntity.status(HttpServletResponse.SC_CREATED)
            .body(concertService.createConcert(request));
    }

    /**
     * 콘서트 전체 조회
     */
    @GetMapping("/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getAllConcerts(PageQuery pageQuery) {
        Page<ConcertSearchResponseDto> concertPage = concertService.getAllConcerts(pageQuery);
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertPage);
    }

    /**
     * 콘서트 수정
     */
    @PostMapping("/concerts/{id}")
    public ResponseEntity<ConcertResponseDto> updateConcert(@PathVariable Integer id,
        @RequestBody ConcertUpdateRequestDto request) {
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertService.updateConcert(id, request));
    }

    /**
     * 콘서트 삭제
     */
    @DeleteMapping("/concerts/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Integer id) {
        concertService.deleteConcert(id);
        return ResponseEntity.status(HttpServletResponse.SC_NO_CONTENT)
            .build();
    }

}