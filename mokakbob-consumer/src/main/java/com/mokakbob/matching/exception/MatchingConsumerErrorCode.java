package com.mokakbob.matching.exception;

import com.mokakbob.matching.common.exception.exceptions.ConsumerErrorCode;

public enum MatchingConsumerErrorCode implements ConsumerErrorCode {

    NOT_FOUND_LOCATION(401, "C001", "매칭하려는 사용자의 위치 정보를 가져올 수 없습니다.")
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
