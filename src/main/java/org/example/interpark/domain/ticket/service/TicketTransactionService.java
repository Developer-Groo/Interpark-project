package org.example.interpark.domain.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketTransactionService {

    private final ConcertRepository concertRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public TicketResponseDto sellTicket(Concert concert, User user) {
        if (concert.getAvailableAmount() <= 0) {
            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
        }

        concert.sellTicket();
        concertRepository.save(concert);

        Ticket ticket = new Ticket(user, concert);
        ticketRepository.save(ticket);

        return TicketResponseDto.from(ticket);
    }
}