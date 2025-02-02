package org.example.interpark.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.repository.ConcertQueryRepository;
import org.example.interpark.util.PageQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertQueryRepository concertQueryRepository;

    /**
     * 검색어 Cache 적용 X
     */
    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcerts(String keyword,
        PageQuery pageQuery) {
        return getConcerts(keyword, pageQuery);
    }

    /**
     * 검색어 Local Memory Cache & Redis Cache 적용
     */
    @Cacheable(value = "concerts", key = "#keyword")
    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsByCache(
        String keyword, PageQuery pageQuery) {
        return getConcerts(keyword, pageQuery);
    }

    private org.example.interpark.util.Page<ConcertSearchResponseDto> getConcerts(String keyword,
        PageQuery pageQuery) {
        Page<ConcertSearchResponseDto> concertList = concertQueryRepository.searchConcerts(keyword,
                pageQuery.toPageable())
            .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
                concert.getTotalAmount(), concert.getAvailableAmount()));

        return org.example.interpark.util.Page.from(concertList);
    }
}
