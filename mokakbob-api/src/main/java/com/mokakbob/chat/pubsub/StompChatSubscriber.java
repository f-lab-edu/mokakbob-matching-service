package com.mokakbob.chat.pubsub;

import com.mokakbob.domain.chat.pubsub.ChatSubscriber;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompChatSubscriber implements ChatSubscriber {

    private static final String CHAT_SUB_ADDRESS = "/sub/chat/";

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handleMessage(String topic, ChatMessageResponse response) {
        long roomId = Long.parseLong(topic.split("\\.")[1]);
        messagingTemplate.convertAndSend(CHAT_SUB_ADDRESS + roomId, response);
    }
}
