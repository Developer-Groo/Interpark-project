package org.example.interpark.domain.ticket.dto;

import org.example.interpark.domain.ticket.entity.Ticket;

public record TicketResponseDto(
        int id
) {
    public static TicketResponseDto from(Ticket ticket) {
        return new TicketResponseDto(ticket.getId());
    }
}
