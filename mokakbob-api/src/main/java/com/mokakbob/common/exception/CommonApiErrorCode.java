package com.mokakbob.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonApiErrorCode implements ApiErrorCode {

    SUCCESS(200, "C000", "요청이 성공하였습니다."),
    INVALID_INPUT_VALUE(400, "C001", "적절하지 않은 입력값입니다."),
    INVALID_TYPE_VALUE(400, "C002", "적절하지 않은 타입입니다."),
    HANDLE_ACCESS_DENIED(403, "C003", "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 내부 오류입니다."),
    METHOD_NOT_ALLOWED(405, "C005", "지원하지 않는 HTTP 메서드입니다."),
    RESOURCE_NOT_FOUND(404, "C006", "요청한 리소스를 찾을 수 없습니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
