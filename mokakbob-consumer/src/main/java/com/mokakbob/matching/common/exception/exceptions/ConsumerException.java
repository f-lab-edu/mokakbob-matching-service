package com.mokakbob.matching.common.exception.exceptions;

public class ConsumerException extends RuntimeException {

    private final ConsumerErrorCode errorCode;

    public ConsumerException(ConsumerErrorCode errorCode) {
        super(errorCode.customCode() + ": " + errorCode.message());
        this.errorCode = errorCode;
    }

    public ConsumerErrorCode consumerErrorCode() {
        return errorCode;
    }
}
