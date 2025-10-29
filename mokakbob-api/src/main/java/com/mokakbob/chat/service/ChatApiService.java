package com.mokakbob.chat.service;

import com.mokakbob.chat.controller.response.ChatMessageResponse;
import com.mokakbob.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatApiService {

    private static final String CHAT_SUB_ADDRESS = "/sub/chat/";

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;

    @Transactional
    public void handleMessage(Long roomId, String memberId, String content) {
        messageService.saveChatMessage(roomId, Long.valueOf(memberId), content);
    }

    public void broadCastMessage(Long roomId, ChatMessageResponse response) {
        messagingTemplate.convertAndSend(CHAT_SUB_ADDRESS + roomId, response);
    }
}
