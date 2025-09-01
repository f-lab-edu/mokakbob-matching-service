package com.mokakbob.common.exception;

public class RedisException extends RuntimeException{

    private final RedisErrorCode errorCode;

    public RedisException(RedisErrorCode errorCode) {
        super(errorCode.customCode() + ": " + errorCode.message());
        this.errorCode = errorCode;
    }

    public RedisErrorCode redisErrorCode() {
        return errorCode;
    }
}
