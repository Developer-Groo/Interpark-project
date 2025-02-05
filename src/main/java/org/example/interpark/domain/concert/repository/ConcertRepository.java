package org.example.interpark.domain.concert.repository;

import org.example.interpark.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Integer> {
    @Query(value = "SELECT available_amount FROM concert WHERE id = :concertId", nativeQuery = true)
    int findLatestAvailableAmount(@Param("concertId") int concertId);
}