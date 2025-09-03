package com.mokakbob.matching.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum MatchingErrorCode implements ApiErrorCode {
    EXIST_MATCHING(400, "M001", "이미 매칭에 참여 중입니다."),
    MATCHING_OPERATION_INTERRUPTED(500, "M002", "매칭 작업이 시스템에 의해 중단되었습니다."),
    NOT_ENOUGH_MATCHING_POINT(400, "M003", "매칭에 참여할 포인트가 부족합니다."),
    ALREADY_PROCESSING_NOTIFICATION(400, "M004", "이미 다른 요청에 의해 처리 중입니다.")
    ;
    private final int httpStatus;
    private final String customCode;
    private final String message;

    MatchingErrorCode(int httpStatus, String customCode, String message) {
        this.httpStatus = httpStatus;
        this.customCode = customCode;
        this.message = message;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String customCode() {
        return customCode;
    }

    @Override
    public String message() {
        return message;
    }
}
