package org.example.interpark.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.search.dto.response.SearchKeywordResponseDto;
import org.example.interpark.domain.search.entity.SearchKeyword;
import org.example.interpark.domain.search.repository.SearchKeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    @Transactional
    public void saveSearchKeyword(String keyword) {
        searchKeywordRepository.findByKeyword(keyword).ifPresentOrElse(
            searchKeyword -> {
                searchKeyword.incrementCount();
                log.info("인기 검색어 카운트 증가: {}", searchKeyword);
            },
            () -> {
                SearchKeyword searchKeyword = new SearchKeyword(keyword, 1);
                searchKeywordRepository.save(searchKeyword);
                log.info("새로운 검색어 카운트 증가: {}", searchKeyword);
            }
        );
    }

    public SearchKeywordResponseDto getTopSearchKeywords() {
        return SearchKeywordResponseDto.from(searchKeywordRepository.findTop10ByOrderByCountDesc());
    }

}
