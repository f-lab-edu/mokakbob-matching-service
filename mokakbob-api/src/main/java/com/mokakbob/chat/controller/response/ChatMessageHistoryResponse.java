package com.mokakbob.chat.controller.response;

import com.mokakbob.domain.chat.domain.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageHistoryResponse(
        Long id,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {

    public static ChatMessageHistoryResponse from(ChatMessage message) {
        return new ChatMessageHistoryResponse(
                message.getId(),
                message.getSenderId(),
                message.getMessage(),
                message.getCreatedAt()
        );
    }
}
