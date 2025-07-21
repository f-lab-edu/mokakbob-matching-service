package com.mokakbob.domain.member.exception;

import com.mokakbob.common.exception.DomainErrorCode;

public enum MemberErrorCode implements DomainErrorCode {
    // auth mail exception
    MAIL_EXCEPTION(400, "E001", "메일 전송 중 오류 발생."),
    NOT_FOUND_MAIL_CODE(404, "E002", "유효한 인증 코드를 찾을 수 없습니다."),
    NOT_MATCH_MAIL_CODE(400, "E003", "인증 코드가 일치하지 않습니다."),
    TOO_MANY_REQUEST(400, "E004", "잠시 후 요청해주세요.")
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
