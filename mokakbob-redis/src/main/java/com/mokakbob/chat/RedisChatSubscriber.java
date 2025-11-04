package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.domain.chat.pubsub.ChatSubscriber;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final ChatSubscriber chatSubscriber;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            ChatMessageResponse payload = objectMapper.readValue(message.getBody(), ChatMessageResponse.class);
            chatSubscriber.handleMessage(channel, payload);
        } catch (Exception e) {
            System.err.println("[RedisChatSubscriber] Failed: " + e.getMessage());
        }
    }
}
