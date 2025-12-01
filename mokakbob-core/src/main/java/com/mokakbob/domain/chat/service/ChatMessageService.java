package com.mokakbob.domain.chat.service;

import com.mokakbob.domain.chat.domain.ChatMessage;
import com.mokakbob.domain.chat.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private static final int DEFAULT_PAGE_OFFSET = 0;

    private final ChatMessageRepository messageRepository;

    @Transactional
    public void saveChatMessage(Long roomId, Long senderId, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(roomId)
                .senderId(senderId)
                .message(message)
                .build();

        messageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> findMessages(Long roomId, LocalDateTime cursor, int sizePlusOne) {
        Pageable pageable = PageRequest.of(DEFAULT_PAGE_OFFSET, sizePlusOne);

        if (cursor == null) {
            return messageRepository.findLatestMessages(roomId, pageable);
        }

        return messageRepository.findMessagesByCursor(roomId, cursor, pageable);
    }
}
