package org.example.interpark.domain.concert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interpark.common.entity.BaseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int totalAmount;
    private int availableAmount;

    @Version
    private int version;

    @Builder
    public Concert(String name, int availableAmount) {
        this.name = name;
        this.availableAmount = availableAmount;
        this.totalAmount = availableAmount;
    }

    public int refundTicket() {
        this.availableAmount += 1;
        return this.availableAmount;
    }


    @Transactional
    @Retryable(maxAttempts = 10)
    public int sellTicket() {
        this.availableAmount -= 1;
        return this.availableAmount;
    }
}
