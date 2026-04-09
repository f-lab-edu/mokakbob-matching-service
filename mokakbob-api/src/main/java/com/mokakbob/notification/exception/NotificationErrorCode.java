package com.mokakbob.notification.exception;

import com.mokakbob.common.exception.BaseErrorCode;

public enum NotificationErrorCode implements BaseErrorCode {

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
