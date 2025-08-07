package com.mokakbob.domain.matching.domain.vo;

import lombok.Getter;

@Getter
public enum MatchingCategory {

    KOREAN("한식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    WESTERN("양식");

    private final String displayName;

    MatchingCategory(String displayName) {
        this.displayName = displayName;
    }
}
