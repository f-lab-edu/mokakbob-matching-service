package com.mokakbob.domain.member.exception;

import com.mokakbob.domain.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements DomainErrorCode {
    ALREADY_EXIST_MEMBER(409, "DMEM001", "이미 존재하는 회원입니다."),
    NOT_FOUND_MEMBER(404, "DMEM002", "회원을 찾을 수 없습니다."),
    NOT_FOUND_MEMBER_BY_EMAIL(404, "DMEM003", "해당 이메일의 회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "DMEM004", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(409, "DMEM005", "이미 사용 중인 닉네임입니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
