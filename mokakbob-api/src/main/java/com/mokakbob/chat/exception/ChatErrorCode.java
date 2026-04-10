package com.mokakbob.chat.exception;

import com.mokakbob.common.exception.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ApiErrorCode {

    NOT_FOUND_CHAT_ROOM(404, "CH001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_JOINED(403, "CH002", "참여하지 않은 채팅방입니다."),
    POINT_NOT_ENOUGH(400, "CH003", "포인트가 부족합니다."),

    STOMP_JWT_MISSING(401, "CH004", "STOMP 연결에 헤더가 누락되었습니다."),
    STOMP_JWT_EXPIRED(401, "CH005", "STOMP 토큰이 만료되었습니다."),
    STOMP_JWT_INVALID(401, "CH006", "STOMP 토큰이 유효하지 않습니다."),
    NOT_SUPPORT_CURSOR_FORMAT(400, "CH007", "지원하지 않는 커서 형식입니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
