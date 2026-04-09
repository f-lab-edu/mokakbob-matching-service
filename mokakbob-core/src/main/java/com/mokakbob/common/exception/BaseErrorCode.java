package com.mokakbob.common.exception;

public interface BaseErrorCode {

    int httpStatus();

    String customCode();

    String message();
}
