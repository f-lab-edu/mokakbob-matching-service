package com.mokakbob.common.exception.exceptions;

public interface ApiErrorCode {

    int httpStatus();

    String customCode();

    String message();
}
