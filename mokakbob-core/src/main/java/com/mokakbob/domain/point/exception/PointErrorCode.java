package com.mokakbob.domain.point.exception;

import com.mokakbob.common.exception.exceptions.DomainErrorCode;

public enum PointErrorCode implements DomainErrorCode {
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    PointErrorCode(int httpStatus, String customCode, String message) {
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
