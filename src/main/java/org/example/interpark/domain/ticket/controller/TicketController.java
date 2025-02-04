package org.example.interpark.domain.ticket.controller;


import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concert/{concert_id}/ticket")
public class TicketController {
    private final TicketService service;

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> find(@PathVariable int concert_id) {
        return ResponseEntity.ok().body(service.find(concert_id));
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<TicketResponseDto>> create(@RequestBody TicketRequestDto dto) {
        return service.createAsync(dto)
                .thenApply(ticketResponse -> ResponseEntity.ok().body(ticketResponse));
    }
}
