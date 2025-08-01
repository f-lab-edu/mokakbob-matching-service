package com.mokakbob.global.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum GlobalErrorCode implements ApiErrorCode {
    INTERNAL_SERVER_ERROR(500, "G001", "내부 서버 문제가 발생하였습니다.")
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    GlobalErrorCode(int httpStatus, String customCode, String message) {
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
