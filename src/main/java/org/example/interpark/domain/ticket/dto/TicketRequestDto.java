package org.example.interpark.domain.ticket.dto;

public record TicketRequestDto(
        int userId,
        int concertId
) {
    public boolean isValid() {
        return userId > 0 && concertId > 0;
    }
}
