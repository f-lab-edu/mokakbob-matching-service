package com.mokakbob.chat;

import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatPublisher implements ChatPublisher {

    private static final String CHANNEL_PREFIX = "chat/";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(Long roomId, ChatMessageResponse response) {
        redisTemplate.convertAndSend(CHANNEL_PREFIX + roomId, response);
    }
}
