package org.example.interpark.domain.concert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {

    @GetMapping("/v1/concerts")
    public ResponseEntity<String> getConcerts() {
        return ResponseEntity.ok("");
    }

    @GetMapping("/v2/concerts")
    public ResponseEntity<String> getConcertsByCache() {
        return ResponseEntity.ok("");
    }
}