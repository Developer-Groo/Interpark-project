package org.example.interpark.domain.ticket.controller;


import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TicketResponseDto> create(@RequestBody TicketRequestDto dto) {
        return ResponseEntity.ok().body(service.create(dto));
    }
}
