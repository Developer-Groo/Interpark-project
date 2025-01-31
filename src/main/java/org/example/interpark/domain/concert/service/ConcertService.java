package org.example.interpark.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.repository.ConcertRepositoryV1;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.example.interpark.util.PageQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertService {

    private final ConcertRepositoryV1 concertRepositoryV1;
    private final SearchKeywordService searchKeywordService;

    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcerts(String keyword,
        PageQuery pageQuery) {
        searchKeywordService.saveSearchKeyword(keyword);

        Page<ConcertSearchResponseDto> concertList = concertRepositoryV1.searchConcerts(keyword,
                pageQuery.toPageable())
            .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
                concert.getAmount()));

        return org.example.interpark.util.Page.from(concertList);
    }

    @Cacheable(value = "concerts", key = "#keyword")
    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsByCache(
        String keyword, PageQuery pageQuery) {
        searchKeywordService.saveSearchKeyword(keyword);

        Page<ConcertSearchResponseDto> concertList = concertRepositoryV1.searchConcerts(keyword,
                pageQuery.toPageable())
            .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
                concert.getAmount()));

        return org.example.interpark.util.Page.from(concertList);
    }
}
