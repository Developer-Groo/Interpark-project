package org.example.interpark.domain.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.lock.service.LockService;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final LockService lockService;

    public TicketResponseDto find(int id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find ticket id: " + id));
        return TicketResponseDto.from(ticket);
    }

    public CompletableFuture<TicketResponseDto> createAsync(TicketRequestDto ticketRequestDto) {
        if (!ticketRequestDto.isValid()) {
            return CompletableFuture.failedFuture(new RuntimeException("Invalid ticket request"));
        }

        CompletableFuture<Concert> concertFuture = CompletableFuture.supplyAsync(() ->
                concertRepository.findById(ticketRequestDto.concertId())
                        .orElseThrow(() -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId())));

        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() ->
                userRepository.findById(ticketRequestDto.userId())
                        .orElseThrow(() -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId())));

        CompletableFuture<Ticket> ticketFuture = concertFuture.thenCombine(userFuture, (concert, user) -> {
            if (concert.getAvailableAmount() <= 0) {
                throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
            }
            return new Ticket(user, concert);
        });


        CompletableFuture<Ticket> savedTicketFuture = ticketFuture.thenCompose(ticket -> {
            return lockService.withLock(ticket.getConcert().getId(), () ->
                    CompletableFuture.supplyAsync(() -> {
                        return saveTicketWithTransaction(ticket);
                    })
            );
        });

        return savedTicketFuture.thenApply(TicketResponseDto::from);
    }

    @Transactional
    public Ticket saveTicketWithTransaction(Ticket ticket) {
        Concert concert = concertRepository.findById(ticket.getConcert().getId())
                .orElseThrow(() -> new RuntimeException("Concert not found"));

        if (concert.getAvailableAmount() <= 0) {
            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
        }

        concert.sellTicket();
        concertRepository.save(concert);
        return ticketRepository.save(ticket);
    }
}