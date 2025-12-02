package com.mokakbob.domain.chat.cache;

import java.time.LocalDateTime;

public record CachedChatMessage(
        Long id,
        Long chatRoomId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
}
