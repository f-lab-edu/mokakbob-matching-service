package com.mokakbob.common.exception.exceptions;

public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.customCode() + ": " + errorCode.message());
        this.errorCode = errorCode;
    }

    public ApiErrorCode apiErrorCode() {
        return errorCode;
    }
}
