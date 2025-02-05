package org.example.interpark.domain.ticket.service;

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
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
        if (!ticketRequestDto.isValid()) {
            throw new RuntimeException("Invalid ticket request");
        }

        int currentTicketAmount = concertRepository.findLatestAvailableAmount(ticketRequestDto.concertId());
        if (currentTicketAmount <= 0) {
            throw new RuntimeException("All tickets had sell.");}

        User user = userRepository.findById(ticketRequestDto.userId()).orElseThrow(
                () -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId()));

        try {
            return lockService.withLock(ticketRequestDto.concertId(), () -> {
                Concert concert = concertRepository.findById(ticketRequestDto.concertId())
                        .orElseThrow(() -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));

                if(concert.getAvailableAmount() <= 0)
                    throw new RuntimeException("Cannot sell ticket. Has no ticket.");

                return sellTicket(concert, user);
            });
        } catch (RuntimeException e) {
            log.error("Failed to create ticket: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TicketResponseDto sellTicket(Concert concert, User user) {
        concert.sellTicket();
        concertRepository.save(concert);

        Ticket ticket = new Ticket(user, concert);
        ticketRepository.save(ticket);

        return TicketResponseDto.from(ticket);
    }
}