package com.mokakbob.global.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum GlobalErrorCode implements ApiErrorCode {
    INTERNAL_SERVER_ERROR(500, "G001", "내부 서버 문제가 발생하였습니다."),
    NOT_FOUND_TOKEN_MEMBER_ID(401, "G002", "토큰에서 사용자 ID를 추출할 수 없습니다."),

    JSON_SERIALIZATION(500, "G003", "이벤트 객체를 JSON으로 변환하는 데 실패했습니다.")
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
