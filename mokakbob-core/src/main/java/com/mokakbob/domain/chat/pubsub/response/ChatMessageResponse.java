package com.mokakbob.domain.chat.pubsub.response;

public record ChatMessageResponse(
        Long chatRoomId,
        String senderId,
        String content
) {
}
