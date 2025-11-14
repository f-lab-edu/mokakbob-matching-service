package com.mokakbob.domain.chat.pubsub.request;

public record ChatMessageRequest(
        Long chatRoomId,
        String content,
        long sentAt
) {
}
