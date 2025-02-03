package org.example.interpark.domain.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;

    public TicketResponseDto find(int id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find ticket id: " + id));
        return TicketResponseDto.from(ticket);
    }

    @Transactional
    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
        if (!ticketRequestDto.isValid()) {
            throw new RuntimeException("Invalid ticket request");
        }

        if (!ticketRepository.existsReservableTicket(ticketRequestDto.concertId())) {
            throw new RuntimeException("Cannot sell ticket. Already all ticket sell.");
        }

        return reserveTicketWithLock(ticketRequestDto);
    }

    public TicketResponseDto reserveTicketWithLock(TicketRequestDto ticketRequestDto) {
        Concert concert = concertRepository.findByIdWithLock(ticketRequestDto.concertId())
                .orElseThrow(() -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));

        if (concert.getAvailableAmount() <= 0) {
            throw new RuntimeException("All ticket sell.");
        }

        concert.sellTicket();
        return publishTicket(ticketRequestDto.userId(), concert);
    }

    public TicketResponseDto publishTicket(int userId, Concert concert) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cannot find user id: " + userId));

        Ticket ticket = new Ticket(user, concert);
        ticket = ticketRepository.save(ticket);

        log.info("Published ticket: {}", ticket);
        return TicketResponseDto.from(ticket);
    }
}