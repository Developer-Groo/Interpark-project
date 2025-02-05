package org.example.interpark.domain.concert.dto.response;

import java.io.Serializable;
import org.example.interpark.domain.concert.entity.Concert;

public record ConcertSearchResponseDto(Integer id, String name, int totalAmount, int availableAmount) implements
    Serializable {

    public static ConcertSearchResponseDto from(Concert concert) {
        return new ConcertSearchResponseDto((concert.getId()), concert.getName(), concert.getTotalAmount(), concert.getAvailableAmount());
    }
}
