package com.mokakbob.chat.controller.request;

public record ChatMessageRequest(
        Long chatRoomId,
        String content
) {
}
