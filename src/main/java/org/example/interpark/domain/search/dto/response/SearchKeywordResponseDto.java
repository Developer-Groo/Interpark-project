package org.example.interpark.domain.search.dto.response;

import java.util.List;
import org.example.interpark.domain.search.entity.SearchKeyword;

public record SearchKeywordResponseDto(List<String> contents) {

    public static SearchKeywordResponseDto from(List<SearchKeyword> searchKeywords) {
        List<String> kewordList = searchKeywords.stream()
            .map(SearchKeyword::getKeyword)
            .toList();

        return new SearchKeywordResponseDto(kewordList);
    }
}
