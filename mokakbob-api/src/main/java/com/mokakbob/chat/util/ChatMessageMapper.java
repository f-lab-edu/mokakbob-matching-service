package com.mokakbob.chat.util;

import com.mokakbob.domain.chat.cache.CachedChatMessage;
import com.mokakbob.domain.chat.domain.ChatMessage;
import java.util.List;

public class ChatMessageMapper {

    public static List<ChatMessage> toChatMessages(List<CachedChatMessage> cachedMessages) {
        return cachedMessages.stream()
                .map(ChatMessageMapper::toChatMessage)
                .toList();
    }

    private static ChatMessage toChatMessage(CachedChatMessage cached) {
        return ChatMessage.builder()
                .chatRoomId(cached.chatRoomId())
                .senderId(cached.senderId())
                .message(cached.content())
                .build();
    }
}
