package com.mokakbob.chat.controller.response;

import com.mokakbob.domain.chat.domain.ChatMessage;
import java.util.List;

public record ChatMessagesResponse(
        Long memberId,
        Long chatRoomId,
        List<ChatMessageHistoryResponse> messages,
        String nextCursor,
        boolean hasNext
) {

    private static final int LAST_MESSAGE_DELIMITER = 1;
    private static final int START_MESSAGE_DELIMITER = 0;
    private static final String CURSOR_DELIMITER = "_";

    public static ChatMessagesResponse of(
            Long memberId,
            Long chatRoomId,
            List<ChatMessage> messages,
            int pageSize
    ) {
        boolean hasNext = false;
        List<ChatMessage> slice = messages;

        if (messages.size() > pageSize) {
            hasNext = true;
            slice = messages.subList(START_MESSAGE_DELIMITER, pageSize);
        }

        List<ChatMessageHistoryResponse> messageResponses = slice.stream()
                .map(ChatMessageHistoryResponse::from)
                .toList();
        String nextCursor = null;

        if (!slice.isEmpty()) {
            ChatMessage oldest = slice.get(slice.size() - LAST_MESSAGE_DELIMITER);
            nextCursor = encodeCursor(oldest);
        }

        return new ChatMessagesResponse(
                memberId,
                chatRoomId,
                messageResponses,
                nextCursor,
                hasNext
        );
    }

    /**
     * 복합 커서 인코딩: createdAt_id
     */
    private static String encodeCursor(ChatMessage message) {
        return message.getCreatedAt().toString() + CURSOR_DELIMITER + message.getId();
    }
}
