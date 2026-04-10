package com.mokakbob.notification.exception;

import com.mokakbob.common.exception.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ApiErrorCode {
    INVALID_NOTIFICATION_TYPE(400, "N001", "유효하지 않은 알림 타입입니다."),
    NOTIFICATION_SEND_FAILED(500, "N002", "알림 전송에 실패했습니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
