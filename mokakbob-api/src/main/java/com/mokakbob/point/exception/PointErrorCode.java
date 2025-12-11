package com.mokakbob.point.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum PointErrorCode implements ApiErrorCode {

    PAYMENT_GATEWAY_ERROR(500, "P001", "결제 승인 서버와의 통신 중 오류가 발생했습니다."),
    INVALID_PAYMENT(400, "P002", "유효하지 않은 결제입니다.")
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    PointErrorCode(int httpStatus, String customCode, String message) {
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
