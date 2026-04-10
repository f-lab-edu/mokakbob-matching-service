package com.mokakbob.domain.matching.exception;

import com.mokakbob.domain.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingErrorCode implements DomainErrorCode {
    MATCHING_NOT_FOUND("M001", "매칭 정보를 찾을 수 없습니다."),
    ALREADY_MATCHED("M002", "이미 매칭된 사용자입니다."),
    NOT_MATCHING_PARTICIPANT("M003", "매칭 참여자가 아닙니다."),
    ;

    private final String customCode;
    private final String message;
}
