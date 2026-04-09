package com.mokakbob.domain.chat.exception;

import com.mokakbob.domain.exception.DomainErrorCode;

public enum ChatErrorCode implements DomainErrorCode {

    NOT_FOUND_CHAT_ROOM(404, "C001", "채팅방을 찾을 수 없습니다.")
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
