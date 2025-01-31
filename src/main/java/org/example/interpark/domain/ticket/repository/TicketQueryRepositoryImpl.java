package org.example.interpark.domain.ticket.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.util.QuerydslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.example.interpark.domain.ticket.entity.QTicket.ticket;

@Repository
@RequiredArgsConstructor
public class TicketQueryRepositoryImpl implements TicketQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Ticket> findAllTickets(Pageable pageable, TicketRequestDto query) {
        List<BooleanExpression> conditions = new ArrayList<>();
        if (query.userId() > 0) {
            conditions.add(ticket.user.id.eq(query.userId()));
        }
        if (query.concertId() > 0) {
            conditions.add(ticket.concert.id.eq(query.concertId()));
        }

        var jpaQuery = queryFactory
                .selectFrom(ticket)
                .leftJoin(ticket.user).fetchJoin()
                .leftJoin(ticket.concert).fetchJoin()
                .where(conditions.toArray(new BooleanExpression[0]));

        return QuerydslUtil.fetchPage(jpaQuery, ticket, pageable);
    }
}
