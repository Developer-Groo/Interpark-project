package org.example.interpark.domain.concert.repository;

import static org.example.interpark.domain.concert.entity.QConcert.*;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.util.QuerydslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Concert> searchConcerts(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(concert.name.containsIgnoreCase(keyword));
        }

        JPAQuery<Concert> result = queryFactory.selectFrom(concert).where(builder);

        return QuerydslUtil.fetchPage(result, concert, pageable);
    }
}
