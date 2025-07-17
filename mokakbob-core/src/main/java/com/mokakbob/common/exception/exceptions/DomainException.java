package com.mokakbob.common.exception.exceptions;

public class DomainException extends RuntimeException {

    private final DomainErrorCode errorCode;

    public DomainException(DomainErrorCode errorCode) {
        super(errorCode.customCode() + ": " + errorCode.message());
        this.errorCode = errorCode;
    }

    public DomainErrorCode domainErrorCode() {
        return errorCode;
    }
}
