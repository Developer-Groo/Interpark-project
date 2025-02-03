package org.example.interpark.domain.concert.dto.response;

import java.io.Serializable;

public record ConcertSearchResponseDto(int id, String name, int totalAmount, int availableAmount) implements
    Serializable {

}
