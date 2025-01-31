package org.example.interpark.domain.search.dto.response;

import org.example.interpark.domain.search.entity.SearchKeyword;

public record SearchKeywordResponseDto(String keyword) {

    public static SearchKeywordResponseDto toDto(SearchKeyword searchKeyword) {
        return new SearchKeywordResponseDto(searchKeyword.getKeyword());
    }
}
