package com.mokakbob.point.exception;

import com.mokakbob.common.exception.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements ApiErrorCode {

    PAYMENT_GATEWAY_ERROR(500, "P001", "결제 승인 서버와의 통신 중 오류가 발생했습니다."),
    INVALID_PAYMENT(400, "P002", "유효하지 않은 결제입니다."),
    PAYMENT_AMOUNT_MISMATCH(400, "P003", "결제 금액이 요청 금액과 일치하지 않습니다."),
    POINT_OPERATION_INTERRUPTED(401, "P004", "결제 과정 중 요류 발생했습니다."),
    EXIST_POINT_CHARGE(400, "P005", "결제 진행중입니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
