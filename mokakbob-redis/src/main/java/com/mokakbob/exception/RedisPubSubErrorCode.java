package com.mokakbob.exception;

import com.mokakbob.common.exception.RedisErrorCode;

public enum RedisPubSubErrorCode implements RedisErrorCode {

    FAIL_REDIS_PUBLISH(500, "P001", "Redis Publish 실패."),
    FAIL_REDIS_SUBSCRIBE(500, "P002", "Redis 메시지 수신 실패."),
    ;
    private final int httpStatus;
    private final String customCode;
    private final String message;

    RedisPubSubErrorCode(int httpStatus, String customCode, String message) {
        this.httpStatus = httpStatus;
        this.customCode = customCode;
        this.message = message;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String customCode() {
        return customCode;
    }

    @Override
    public String message() {
        return message;
    }
}
