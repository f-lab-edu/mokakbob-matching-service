package com.mokakbob.domain.point.exception;

import com.mokakbob.domain.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements DomainErrorCode {
    INVALID_PAYMENT(400, "DP002", "유효하지 않은 결제입니다."),
    DUPLICATE_PAYMENT(409, "DP004", "이미 승인된 결제입니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
