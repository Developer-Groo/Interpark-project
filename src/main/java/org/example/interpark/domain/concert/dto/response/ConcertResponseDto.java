package org.example.interpark.domain.concert.dto.response;

import org.example.interpark.domain.concert.entity.Concert;

public record ConcertResponseDto (int id, String name, int totalAmount, int availableAmount){

  public static ConcertResponseDto from(Concert concert) {
    return new ConcertResponseDto((concert.getId()), concert.getName(), concert.getTotalAmount(), concert.getAvailableAmount());
  }

}
