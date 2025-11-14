package com.mokakbob.chat.service;

import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatApiService {

    private final ChatPublisher chatPublisher;
    private final ChatMessageService messageService;

    @Transactional
    public void handleMessage(Long roomId, String memberId, String content, long sendAt) {
        messageService.saveChatMessage(roomId, Long.valueOf(memberId), content);
        chatPublisher.publish(roomId, new ChatMessageResponse(roomId, memberId, content, sendAt));
    }
}
