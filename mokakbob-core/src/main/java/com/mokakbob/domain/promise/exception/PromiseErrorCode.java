package com.mokakbob.domain.promise.exception;

import com.mokakbob.common.exception.DomainErrorCode;

public enum PromiseErrorCode implements DomainErrorCode {
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    PromiseErrorCode(int httpStatus, String customCode, String message) {
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
