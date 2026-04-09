package com.mokakbob.chat.exception;

import com.mokakbob.common.exception.BaseErrorCode;

public enum ChatErrorCode implements BaseErrorCode {

    // STOMP
    STOMP_JWT_MISSING(401, "CHAT_001", "Authorization 헤더가 없습니다."),
    STOMP_JWT_INVALID(401, "CHAT_002", "유효하지 않은 JWT 토큰입니다."),
    STOMP_JWT_EXPIRED(401, "CHAT_003", "JWT 토큰이 만료되었습니다."),

    // MESSAGE
    NOT_SUPPORT_CURSOR_FORMAT(401, "MESSAGE_001", "잘못된 커서 요청입니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    ChatErrorCode(int httpStatus, String customCode, String message) {
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
