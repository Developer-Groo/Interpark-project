package org.example.interpark.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.dto.request.ConcertCreateRequestDto;
import org.example.interpark.domain.concert.dto.request.ConcertUpdateRequestDto;
import org.example.interpark.domain.concert.dto.response.ConcertResponseDto;
import org.example.interpark.domain.concert.dto.response.ConcertSearchResponseDto;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.concert.repository.ConcertRepositoryV1;
import org.example.interpark.util.Page;
import org.example.interpark.util.PageQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConcertService {

  private final ConcertPopularSearchService concertPopularSearchService;
  private final ConcertCacheService concertCacheService;
  private final ConcertRepository concertRepository;
  private final ConcertRepositoryV1 concertRepositoryV1;


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

    var concertList = concertRepositoryV1.getAllConcerts(pageable)
        .map(concert -> new ConcertSearchResponseDto(
            concert.getId(),
            concert.getName(),
            concert.getTotalAmount(),
            concert.getAvailableAmount()
        ));

    return Page.from(concertList);
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
      concertPopularSearchService.incrementSearchCount(keyword);
    }
    return concertCacheService.searchConcertsByCache(keyword,pageQuery);
  }


}
