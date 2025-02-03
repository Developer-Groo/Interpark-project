package org.example.interpark.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.request.ConcertCreateRequestDto;
import org.example.interpark.domain.concert.dto.request.ConcertUpdateRequestDto;
import org.example.interpark.domain.concert.dto.response.ConcertResponseDto;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertQueryRepository;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertQueryRepository concertQueryRepository;
    private final ConcertRepository concertRepository;

    /**
     * Cache 를 적용하지 않은 콘서트 조회
     */
    public Page<ConcertSearchResponseDto> searchConcerts(String keyword,
        PageQuery pageQuery) {
        org.springframework.data.domain.Page<ConcertSearchResponseDto> concertList = concertQueryRepository.searchConcerts(
                keyword, pageQuery.toPageable())
            .map(ConcertSearchResponseDto::from);
        return Page.from(concertList);
    }

    /**
     * 콘서트 생성
     */
    @Transactional
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

    /**
     * 모든 콘서트 조회
     */
    public Page<ConcertSearchResponseDto> getAllConcerts(PageQuery pageQuery) {
        org.springframework.data.domain.Page<ConcertSearchResponseDto> concertList = concertRepository.findAll(
                pageQuery.toPageable())
            .map(ConcertSearchResponseDto::from);

        return Page.from(concertList);
    }

    /**
     * 콘서트 수정
     */
    @Transactional
    public ConcertResponseDto updateConcert(Integer id, ConcertUpdateRequestDto request) {
        Concert concert = concertRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 콘서트가 없습니다."));

        concert.updateConcert(request.name(), request.totalAmount(), request.availableAmount());
        return ConcertResponseDto.from(concert);
    }

    /**
     * 콘서트 삭제
     */
    @Transactional
    public void deleteConcert(Integer id) {
        concertRepository.deleteById(id);
    }
}