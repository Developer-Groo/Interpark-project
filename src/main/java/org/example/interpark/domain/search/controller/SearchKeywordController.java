package org.example.interpark.domain.search.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.search.dto.response.SearchKeywordResponseDto;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchKeywordController {

    private final SearchKeywordService searchKeywordService;

    @GetMapping("/api/v1/keywords/popular")
    public ResponseEntity<List<SearchKeywordResponseDto>> getTopSearchKeywords() {
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(searchKeywordService.getTopSearchKeywords());
    }
}
