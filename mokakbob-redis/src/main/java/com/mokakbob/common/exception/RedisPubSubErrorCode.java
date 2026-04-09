package com.mokakbob.common.exception;

public enum RedisPubSubErrorCode implements RedisErrorCode {

    REDIS_PUBLISH_ERROR(500, "R003", "레디스 메시지 발행 중 오류가 발생했습니다."),
    REDIS_SUBSCRIBE_ERROR(500, "R004", "레디스 메시지 구독 중 오류가 발생했습니다."),
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
