package org.example.interpark.domain.concert.dto.response;

import org.example.interpark.domain.concert.entity.Concert;

import java.io.Serializable;

public record ConcertSearchResponseDto(int id, String name, int totalAmount, int availableAmount) implements
    Serializable {

    public static ConcertSearchResponseDto from(Concert concert) {
        return new ConcertSearchResponseDto((concert.getId()), concert.getName(), concert.getTotalAmount(), concert.getAvailableAmount());
    }
}
