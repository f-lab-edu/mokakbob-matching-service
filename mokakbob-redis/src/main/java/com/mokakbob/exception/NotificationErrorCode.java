package com.mokakbob.exception;

import com.mokakbob.common.exception.RedisErrorCode;

public enum NotificationErrorCode implements RedisErrorCode {

    FAIL_REDIS_OPERATION(500, "R001", "Redis 조회 및 저장 오류가 발생했습니다."),
    NOT_FOUND_NOTIFICATION(500, "R002", "저장된 알림이 없습니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    NotificationErrorCode(int httpStatus, String customCode, String message) {
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
