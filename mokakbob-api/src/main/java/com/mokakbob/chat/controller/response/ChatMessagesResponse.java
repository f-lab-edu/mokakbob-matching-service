package com.mokakbob.chat.controller.response;

import com.mokakbob.domain.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;

public record ChatMessagesResponse(
        Long memberId,
        Long chatRoomId,
        List<ChatMessageHistoryResponse> messages,
        String nextCursor,
        boolean hasNext
) {

    private static final int CHAT_LAST_MESSAGE_MAKER = 1;

    public static ChatMessagesResponse of(
            Long memberId,
            Long chatRoomId,
            List<ChatMessage> messages
    ) {
        List<ChatMessageHistoryResponse> messageResponses = messages.stream()
                .map(ChatMessageHistoryResponse::from)
                .toList();
        String nextCursor = null;
        boolean hasNext = false;

        if (!messages.isEmpty()) {
            ChatMessage oldest = messages.get(messages.size() - CHAT_LAST_MESSAGE_MAKER);
            LocalDateTime cursor = oldest.getCreatedAt();
            nextCursor = cursor.toString();
            hasNext = true;
        }

        return new ChatMessagesResponse(
                memberId,
                chatRoomId,
                messageResponses,
                nextCursor,
                hasNext
        );
    }
}
