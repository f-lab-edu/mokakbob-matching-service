package com.mokakbob.common.exception;

public class RedisException extends BaseException {

    public RedisException(RedisErrorCode errorCode) {
        super(errorCode);
    }

    public RedisException(RedisErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    @Override
    public RedisErrorCode getErrorCode() {
        return (RedisErrorCode) super.getErrorCode();
    }
}
