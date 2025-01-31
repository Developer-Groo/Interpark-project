package org.example.interpark.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.repository.ConcertRepositoryV1;
import org.example.interpark.util.PageQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepositoryV1 concertRepositoryV1;

    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcerts(String keyword,
        PageQuery pageQuery) {
        Page<ConcertSearchResponseDto> concertList = concertRepositoryV1.searchConcerts(keyword,
                pageQuery.toPageable())
            .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
                concert.getAmount()));

        return org.example.interpark.util.Page.from(concertList);
    }
}
