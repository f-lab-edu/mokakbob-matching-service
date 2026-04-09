package com.mokakbob.matching.common.exception;

import com.mokakbob.common.exception.BaseException;

public class ConsumerException extends BaseException {

    public ConsumerException(ConsumerErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public ConsumerErrorCode getErrorCode() {
        return (ConsumerErrorCode) super.getErrorCode();
    }
}
