package com.mokakbob.common.exception;

import com.mokakbob.common.exception.BaseErrorCode;

public enum ApiErrorCode implements BaseErrorCode {
    INVALID_INPUT_VALUE(400, "API-001", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "API-002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(500, "API-003", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(400, "API-004", "요청 타입이 올바르지 않습니다."),
    HANDLE_ACCESS_DENIED(403, "API-005", "접근 권한이 없습니다.");

    private final int httpStatus;
    private final String customCode;
    private final String message;

    ApiErrorCode(int httpStatus, String customCode, String message) {
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
