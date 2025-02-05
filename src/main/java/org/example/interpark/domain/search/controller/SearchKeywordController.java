package org.example.interpark.domain.search.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.search.dto.response.SearchKeywordResponseDto;
import org.example.interpark.domain.search.service.RedisSearchKeywordService;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchKeywordController {

    private final SearchKeywordService searchKeywordService;
    private final RedisSearchKeywordService redisSearchKeywordService;

    @GetMapping("/v1/keywords/popular")
    public ResponseEntity<SearchKeywordResponseDto> getPopularKeywords() {
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(searchKeywordService.getTopSearchKeywords());
    }

    @GetMapping("/v2/keywords/popular")
    public ResponseEntity<List<String>> getPopularKeywordsByRedis() {
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(redisSearchKeywordService.getTopSearchKeywords());
    }
}
