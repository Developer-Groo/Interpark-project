package org.example.interpark.domain.search.repository;

import java.util.List;
import java.util.Optional;
import org.example.interpark.domain.search.entity.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    Optional<SearchKeyword> findByKeyword(String keyword);

    List<SearchKeyword> findTop10ByOrderByCountDesc();
}
