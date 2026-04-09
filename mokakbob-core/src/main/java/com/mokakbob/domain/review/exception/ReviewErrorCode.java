package com.mokakbob.domain.review.exception;

import com.mokakbob.domain.exception.DomainErrorCode;

public enum ReviewErrorCode implements DomainErrorCode {
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    ReviewErrorCode(int httpStatus, String customCode, String message) {
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
