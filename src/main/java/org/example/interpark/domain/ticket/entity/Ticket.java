package org.example.interpark.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interpark.common.entity.BaseEntity;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.user.entity.User;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "ticket_seq_generator",
        sequenceName = "ticket_seq",
        initialValue = 1,
        allocationSize = 1000
)
public class Ticket extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq_generator")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    public Ticket(User user, Concert concert) {
        this.user = user;
        this.concert = concert;
    }
}
