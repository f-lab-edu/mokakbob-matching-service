package com.mokakbob.domain.member.exception;

import com.mokakbob.common.exception.DomainErrorCode;

public enum MemberErrorCode implements DomainErrorCode {
    DUPLICATE_EMAIL(409, "M001", "중복되는 이메일입니다."),
    DUPLICATE_NICKNAME(409, "M002", "중복되는 이메일입니다."),
    NOT_FOUND_MEMBER_BY_EMAIL(404, "M003", "이메일에 해당하는 유저 정보가 없습니다.")
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
