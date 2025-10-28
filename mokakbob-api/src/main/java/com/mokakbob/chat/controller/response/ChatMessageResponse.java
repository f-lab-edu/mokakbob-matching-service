package com.mokakbob.chat.controller.response;

public record ChatMessageResponse(
        Long chatRoomId,
        String nickName,
        String content
) {
}
