package com.mokakbob.matching.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum MatchingErrorCode implements ApiErrorCode {
    EXIST_MATCHING(400, "M001", "이미 매칭에 참여 중입니다.")
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
