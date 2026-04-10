package com.mokakbob.common.exception;

public abstract class BaseException extends RuntimeException {

    private final BaseErrorCode errorCode;

    protected BaseException(BaseErrorCode errorCode) {
        super(errorCode.getCustomCode() + ": " + errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
