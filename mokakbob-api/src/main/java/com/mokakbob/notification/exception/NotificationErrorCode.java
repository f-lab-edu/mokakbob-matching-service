package com.mokakbob.notification.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum NotificationErrorCode implements ApiErrorCode {

    ALREADY_RESPONDED(400, "N001", "이미 응답한 알림입니다."),
    NOT_FOUND_NOTIFICATION(404, "N002", "알림을 찾을 수 없습니다."),
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
