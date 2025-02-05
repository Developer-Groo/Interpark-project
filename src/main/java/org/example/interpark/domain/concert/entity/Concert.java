package org.example.interpark.domain.concert.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interpark.common.entity.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Concert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int totalAmount;
    private int availableAmount;

    @Builder
    public Concert(String name,int totalAmount, int availableAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.availableAmount = availableAmount;
    }

    public void updateConcert(String name, int totalAmount, int availableAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.availableAmount = availableAmount;
    }

    public int refundTicket() {
        this.availableAmount += 1;
        return this.availableAmount;
    }

    public int sellTicket() {
        this.availableAmount -= 1;
        return this.availableAmount;
    }
}
