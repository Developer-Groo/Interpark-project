package org.example.interpark.domain.search.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.search.dto.response.SearchKeywordResponseDto;
import org.example.interpark.domain.search.entity.SearchKeyword;
import org.example.interpark.domain.search.repository.SearchKeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                searchKeywordRepository.save(searchKeyword);
            },
            () -> {
                SearchKeyword searchKeyword = new SearchKeyword(keyword, 1);
                searchKeywordRepository.save(searchKeyword);
            }
        );
    }

    public List<SearchKeywordResponseDto> getTopSearchKeywords() {
        return searchKeywordRepository.findTop10ByOrderByCountDesc().stream()
            .map(SearchKeywordResponseDto::toDto).toList();
    }

}
