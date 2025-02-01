package org.example.interpark.domain.ticket.controller;


import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.service.LettuceTicketLockService;
import org.example.interpark.domain.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concert/{concert_id}/ticket")
public class TicketController {

    private final TicketService service;
    private final LettuceTicketLockService ticketLockService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> find(@PathVariable int concert_id) {
        return ResponseEntity.ok().body(service.find(concert_id));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> create(@RequestBody TicketRequestDto dto) {
        return ResponseEntity.ok().body(ticketLockService.decrease(dto));
    }
}
