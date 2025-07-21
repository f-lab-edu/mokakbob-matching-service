package com.mokakbob.domain.member.exception;

import com.mokakbob.common.exception.DomainErrorCode;

public enum MemberErrorCode implements DomainErrorCode {
    // auth mail exception
    MAIL_EXCEPTION(400, "E001", "메일 전송 중 오류 발생.")
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    MemberErrorCode(int httpStatus, String customCode, String message) {
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
