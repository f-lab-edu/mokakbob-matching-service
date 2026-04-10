package com.mokakbob.domain.chat.exception;

import com.mokakbob.domain.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements DomainErrorCode {
    NOT_FOUND_CHAT_ROOM(404, "DCH001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_JOINED(403, "DCH002", "참여하지 않은 채팅방입니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
