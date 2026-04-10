package com.mokakbob.matching.exception;

import com.mokakbob.common.exception.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingErrorCode implements ApiErrorCode {
    MATCHING_NOT_FOUND(404, "M001", "매칭 정보를 찾을 수 없습니다."),
    ALREADY_MATCHED(400, "M002", "이미 매칭된 사용자입니다."),
    NOT_MATCHED(400, "M003", "매칭되지 않은 사용자입니다."),

    EXIST_MATCHING(400, "M004", "이미 진행 중인 매칭이 존재합니다."),
    MATCHING_OPERATION_INTERRUPTED(500, "M005", "매칭 처리 중 인터럽트가 발생했습니다."),
    NOT_ENOUGH_MATCHING_POINT(400, "M006", "매칭을 위한 포인트가 부족합니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
