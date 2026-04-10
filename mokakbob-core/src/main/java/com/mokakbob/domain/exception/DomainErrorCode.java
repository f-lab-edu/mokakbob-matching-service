package com.mokakbob.domain.exception;

import com.mokakbob.common.exception.BaseErrorCode;

public interface DomainErrorCode extends BaseErrorCode {

    /**
     * 도메인 에러의 HTTP 상태 코드를 반환합니다.
     * 각 enum에서 httpStatus 필드로 오버라이드합니다.
     */
    int getHttpStatus();
}
