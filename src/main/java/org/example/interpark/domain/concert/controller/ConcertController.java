package org.example.interpark.domain.concert.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.service.ConcertService;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/v1/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcerts(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        return ResponseEntity.status(HttpServletResponse.SC_OK)
            .body(concertService.searchConcerts(keyword, pageQuery));
    }

    @GetMapping("/v2/concerts")
    public ResponseEntity<Page<ConcertSearchResponseDto>> getConcertsByCache(@RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        return ResponseEntity.ok(concertService.searchConcertsByCache(keyword, pageQuery));
    }
}