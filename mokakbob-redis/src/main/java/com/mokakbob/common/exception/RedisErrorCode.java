package com.mokakbob.common.exception;

public interface RedisErrorCode {

    int httpStatus();

    String customCode();

    String message();
}
