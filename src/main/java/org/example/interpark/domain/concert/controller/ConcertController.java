package org.example.interpark.domain.concert.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.request.ConcertCreateRequestDto;
import org.example.interpark.domain.concert.dto.request.ConcertUpdateRequestDto;
import org.example.interpark.domain.concert.dto.response.ConcertResponseDto;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.service.ConcertCacheService;
import org.example.interpark.domain.concert.service.ConcertPopularSearchService;
import org.example.interpark.domain.concert.service.ConcertService;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {

    private final ConcertService concertService;
    private final ConcertCacheService concertCacheService;
    private final ConcertPopularSearchService concertPopularSearchService;

    // 콘서트 생성
    @PostMapping("/concerts")
    public ConcertResponseDto createConcert(@RequestBody ConcertCreateRequestDto request) {
        return concertService.createConcert(request);
    }
    
    // 콘서트 전체조회
    @GetMapping("/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getAllConcerts(PageQuery pageQuery) {
        Page<ConcertSearchResponseDto> concertPage = concertService.getAllConcerts(pageQuery);
        return ResponseEntity.ok(concertPage);
    }

    // 콘서트 수정
    @PutMapping("/concerts/{id}")
    public ConcertResponseDto updateConcert(@PathVariable Integer id, @RequestBody ConcertUpdateRequestDto request) {
        return concertService.updateConcert(id, request);
    }

    // 콘서트 삭제
    @DeleteMapping("/concerts/{id}")
    public void deleteConcert(@PathVariable Integer id) {
        concertService.deleteConcert(id);
    }

    // 특정 콘서트 검색
    @GetMapping("/v1/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcerts(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertCacheService.searchConcerts(keyword, pageQuery));
    }

    // String 기반 검색 API
    @GetMapping("/v2/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcertsByCache(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {

//        if (keyword != null && !keyword.isBlank()) {
//            concertPopularSearchService.incrementSearchCount(keyword);
//        }
        return ResponseEntity.ok(concertService.searchConcertsWithCount(keyword, pageQuery));
    }

    // Hash 기반 검색 API
    @GetMapping("/v3/concerts")
    public ResponseEntity<List<ConcertSearchResponseDto>> getConcertsFromCache(
        @RequestParam(required = false) String keyword, PageQuery pageQuery) {

        return ResponseEntity.ok(concertCacheService.searchConcertsByHash(keyword, pageQuery));
    }

    // 인기 검색어
    @GetMapping("/v2/concerts/popular")
    public ResponseEntity<List<String>> getPopularKeywords() {
        List<String> topSearchKeywords = concertPopularSearchService.getTopSearchKeywords();
        return ResponseEntity.ok(topSearchKeywords);
    }
}