package com.mokakbob.auth.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum AuthApiErrorCode implements ApiErrorCode {
    // auth mail exception
    NOT_MATCH_MAIL_CODE(400, "E001", "인증 코드가 일치하지 않습니다."),
    TOO_MANY_REQUEST(400, "E002", "잠시 후 요청해주세요."),
    NOT_EXIST_MAIL_CODE(404, "E003", "코드 값이 존재하지 않습니다."),

    // token exception
    TOKEN_EXPIRED(401, "JWT001", "토큰이 만료되었습니다."),
    TOKEN_INVALID_SIGNATURE(401, "JWT002", "토큰 서명이 유효하지 않습니다."),
    TOKEN_INVALID(401, "JWT003", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(404, "JWT004", "토큰을 찾을 수 없습니다."),
    TOKEN_NOT_EXPIRED(400, "JWT005", "accessToken 이 아직 유효합니다."),

    // auth login exception
    NOT_MATCH_PASSWORD(401, "L001", "비밀번호가 일치하지 않습니다."),

    // oauth security
    FORBIDDEN(403, "OAUTH001", "권한이 없습니다.")
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    AuthApiErrorCode(int httpStatus, String customCode, String message) {
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
