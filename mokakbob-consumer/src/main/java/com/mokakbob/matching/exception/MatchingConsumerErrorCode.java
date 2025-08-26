package com.mokakbob.matching.exception;

import com.mokakbob.matching.common.exception.exceptions.ConsumerErrorCode;

public enum MatchingConsumerErrorCode implements ConsumerErrorCode {

    ALREADY_PARTICIPATE_MATCHING(400, "C001", "이미 매칭 참여중입니다.")
    ;
    private final int httpStatus;
    private final String customCode;
    private final String message;

    MatchingConsumerErrorCode(int httpStatus, String customCode, String message) {
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
