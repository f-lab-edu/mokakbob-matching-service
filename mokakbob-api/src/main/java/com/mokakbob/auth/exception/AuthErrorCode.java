package com.mokakbob.auth.exception;

import com.mokakbob.common.exception.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ApiErrorCode {

    TOKEN_NOT_FOUND(404, "A001", "토큰을 찾을 수 없습니다."),
    TOKEN_EXPIRED(401, "A002", "토큰이 만료되었습니다."),
    TOKEN_INVALID(401, "A003", "유효하지 않은 토큰입니다."),
    TOKEN_MALFORMED(401, "A005", "토큰의 형식이 잘못되었습니다."),
    TOKEN_UNSUPPORTED(401, "A006", "지원하지 않는 토큰입니다."),
    TOKEN_CLAIM_INVALID(401, "A007", "토큰의 클레임이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(401, "A008", "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_NOT_FOUND(404, "A009", "리프레시 토큰을 찾을 수 없습니다."),

    LOGOUT_ACCESS_TOKEN(401, "A010", "로그아웃된 토큰입니다."),
    TOKEN_INVALID_SIGNATURE(401, "A011", "토큰 서명이 유효하지 않습니다."),
    INVALID_CREDENTIALS(401, "A012", "이메일 또는 비밀번호가 일치하지 않습니다."),
    NOT_EXIST_MAIL_CODE(400, "A013", "인증 코드가 존재하지 않습니다."),
    NOT_MATCH_MAIL_CODE(400, "A014", "인증 코드가 일치하지 않습니다."),
    TOO_MANY_REQUEST(429, "A015", "너무 많은 요청이 발생했습니다."),
    TOKEN_NOT_EXPIRED(400, "A016", "토큰이 아직 만료되지 않았습니다."),
    FORBIDDEN(403, "A017", "권한이 없습니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
