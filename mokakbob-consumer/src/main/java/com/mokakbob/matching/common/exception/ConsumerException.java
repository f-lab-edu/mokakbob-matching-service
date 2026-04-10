package com.mokakbob.matching.common.exception;

import com.mokakbob.common.exception.BaseException;

public class ConsumerException extends BaseException {

    public ConsumerException(ConsumerErrorCode errorCode) {
        super(errorCode);
    }

    public ConsumerException(ConsumerErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    @Override
    public ConsumerErrorCode getErrorCode() {
        return (ConsumerErrorCode) super.getErrorCode();
    }
}
