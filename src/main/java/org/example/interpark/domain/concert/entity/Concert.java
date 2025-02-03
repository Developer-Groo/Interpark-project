package org.example.interpark.domain.concert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interpark.common.entity.BaseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

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


    public void sellTicket() throws ObjectOptimisticLockingFailureException {
        if (this.availableAmount <= 0) {
            throw new OptimisticLockException("No available tickets left!");
        }
        this.availableAmount -= 1;
    }


}

