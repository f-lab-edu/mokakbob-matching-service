package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.domain.chat.pubsub.ChatSubscriber;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.metrix.ChatMetrics;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatSubscriber implements MessageListener {

    private static final String METRICS_EVENT = "chat_receive";

    private final ObjectMapper objectMapper;
    private final ChatSubscriber chatSubscriber;
    private final ChatMetrics chatMetrics;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        long start = System.currentTimeMillis();

        try {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            ChatMessageResponse payload = objectMapper.readValue(message.getBody(), ChatMessageResponse.class);
            chatSubscriber.handleMessage(channel, payload);
        } catch (Exception e) {
            chatMetrics.countError(METRICS_EVENT);
        } finally {
            chatMetrics.recordLatency(METRICS_EVENT, System.currentTimeMillis() - start);
        }
    }
}
