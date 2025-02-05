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

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final LockService lockService;
    private final TicketTransactionService transactionService;

    public TicketResponseDto find(int id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find ticket id: " + id));
        return TicketResponseDto.from(ticket);
    }

    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
        if (!ticketRequestDto.isValid()) {
            throw new RuntimeException("Invalid ticket request");
        }

        User user = userRepository.findById(ticketRequestDto.userId()).orElseThrow(
            () -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId()));

        /**
         *
         Concert beforeCheckConcert = concertRepository.findById(ticketRequestDto.concertId())
            .orElseThrow(() -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));
         if(beforeCheckConcert.getAvailableAmount() <= 0)
            throw new RuntimeException("Cannot buy concert ticket.");

         * Lock 이 걸리기 전 Concert 정보를 먼저 가져와서 availableAmount 양을 검사 시, Lock 이 제대로 동작하지 않습니다.
         * 그 이유는...
         */

        try {
            return lockService.withLock(ticketRequestDto.concertId(), () -> {
                Concert concert = concertRepository.findById(ticketRequestDto.concertId())
                    .orElseThrow(() -> new RuntimeException(
                        "Cannot find concert id: " + ticketRequestDto.concertId()));

                if (concert.getAvailableAmount() <= 0) {
                    throw new RuntimeException(
                        "Cannot sell ticket. Available amount is less than 0.");
                }

                return transactionService.sellTicket(concert, user);
            });
        } catch (RuntimeException e) {
            log.error("Failed to create ticket: {}", e.getMessage());
            throw e;
        }
    }
}