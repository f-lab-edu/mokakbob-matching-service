package com.mokakbob.common.exception;

public class RedisException extends BaseException {

    public RedisException(RedisErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public RedisErrorCode getErrorCode() {
        return (RedisErrorCode) super.getErrorCode();
    }
}
