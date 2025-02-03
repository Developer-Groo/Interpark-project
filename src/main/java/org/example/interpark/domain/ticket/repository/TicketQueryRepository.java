package org.example.interpark.domain.ticket.repository;

import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TicketQueryRepository {
    Page<Ticket> findAllTickets(Pageable pageable, TicketRequestDto query);
    Boolean existsReservableTicket(int concertId);
}
