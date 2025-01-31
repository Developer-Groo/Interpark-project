package org.example.interpark.domain.concert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interpark.common.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Concert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int totalAmount;
    private int availableAmount;

    @Builder
    public Concert(String name, int amount) {
        this.name = name;
        this.availableAmount = amount;
    }

    public int refundTicket() {
        return this.availableAmount + 1;
    }

    public int sellTicket() {
        return this.availableAmount - 1;
    }
}
