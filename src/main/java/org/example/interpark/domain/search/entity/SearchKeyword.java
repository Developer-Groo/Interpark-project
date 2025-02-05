package org.example.interpark.domain.search.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchKeyword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String keyword;

    private int count;

    public SearchKeyword(String keyword, int count) {
        this.keyword = keyword;
        this.count = count;
    }

    public void incrementCount() {
        count++;
    }
}
