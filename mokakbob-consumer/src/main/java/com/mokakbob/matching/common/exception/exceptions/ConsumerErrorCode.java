package com.mokakbob.matching.common.exception.exceptions;

public interface ConsumerErrorCode {

    int httpStatus();

    String customCode();

    String message();
}
