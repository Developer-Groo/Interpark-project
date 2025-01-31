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

    private int amount;

    @Builder
    public Concert(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }
}
