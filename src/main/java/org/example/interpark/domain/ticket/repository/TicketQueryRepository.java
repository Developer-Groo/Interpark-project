package org.example.interpark.domain.ticket.repository;

import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketQueryRepository {
    Page<Ticket> findAllTickets(Pageable pageable, TicketRequestDto query);

    int countAllTicketsByConcertId(int concertId);
}
