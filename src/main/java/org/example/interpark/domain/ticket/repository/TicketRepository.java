package org.example.interpark.domain.ticket.repository;

import org.example.interpark.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer>, TicketQueryRepository {
}
