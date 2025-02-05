package org.example.interpark.domain.search.dto.response;

import org.example.interpark.domain.search.entity.SearchKeyword;

import java.util.List;

public record SearchKeywordResponseDto(List<String> contents) {

    public static SearchKeywordResponseDto from(List<SearchKeyword> searchKeywords) {
        List<String> kewordList = searchKeywords.stream()
            .map(SearchKeyword::getKeyword)
            .toList();

        return new SearchKeywordResponseDto(kewordList);
    }
}
