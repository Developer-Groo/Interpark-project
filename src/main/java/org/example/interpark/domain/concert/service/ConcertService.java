package org.example.interpark.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.request.ConcertCreateRequestDto;
import org.example.interpark.domain.concert.dto.request.ConcertUpdateRequestDto;
import org.example.interpark.domain.concert.dto.response.ConcertResponseDto;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertQueryRepository;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.search.service.RedisSearchKeywordService;
import org.example.interpark.domain.search.service.SearchKeywordService;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertService {

    private final ConcertQueryRepository concertQueryRepository;
    private final ConcertCacheService concertCacheService;
    private final ConcertRepository concertRepository;
    private final SearchKeywordService searchKeywordService;
    private final RedisSearchKeywordService redisSearchKeywordService;


    // 콘서트 생성
    public ConcertResponseDto createConcert(ConcertCreateRequestDto request) {
        Concert concert = concertRepository.save(
            Concert.builder()
                .name(request.name())
                .totalAmount(request.totalAmount())
                .availableAmount(request.availableAmount())
                .build()
        );
        return ConcertResponseDto.from(concert);
    }

    public Page<ConcertSearchResponseDto> getAllConcerts(PageQuery pageQuery) {
        Pageable pageable = pageQuery.toPageable();

        var concertList = concertQueryRepository.getAllConcerts(pageable)
            .map(concert -> new ConcertSearchResponseDto(
                concert.getId(),
                concert.getName(),
                concert.getTotalAmount(),
                concert.getAvailableAmount()
            ));

        return Page.from(concertList);
    }

    /**
     * Cache 적용 X
     */
    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcerts(String keyword,
        PageQuery pageQuery) {
        searchKeywordService.saveSearchKeyword(keyword);

        org.springframework.data.domain.Page<ConcertSearchResponseDto> concertList = concertQueryRepository.searchConcerts(keyword,
                pageQuery.toPageable())
            .map(concert -> new ConcertSearchResponseDto(concert.getId(), concert.getName(),
                concert.getTotalAmount(), concert.getAvailableAmount()));

        return org.example.interpark.util.Page.from(concertList);
    }

    // 콘서트 수정
    public ConcertResponseDto updateConcert(Integer id, ConcertUpdateRequestDto request) {
        Concert concert = concertRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 콘서트가 없습니다."));

        concert.updateConcert(request.name(), request.totalAmount(), request.availableAmount());
        return ConcertResponseDto.from(concert);
    }

    // 콘서트 삭제
    public void deleteConcert(Integer id) {
        concertRepository.deleteById(id);
    }

    public org.example.interpark.util.Page<ConcertSearchResponseDto> searchConcertsWithCount(
        String keyword, PageQuery pageQuery) {
        if (keyword != null && !keyword.isBlank()) {
            redisSearchKeywordService.incrementSearchCount(keyword);
        }
        return concertCacheService.searchConcertsByCache(keyword,pageQuery);
    }
}