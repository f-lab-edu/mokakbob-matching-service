package com.mokakbob.domain.chat.service;

import com.mokakbob.domain.chat.domain.ChatMessage;
import com.mokakbob.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

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
}
