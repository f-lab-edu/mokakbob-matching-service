package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.config.RedisConstants;
import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.common.exception.RedisPubSubErrorCode;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatPublisher implements ChatPublisher {

    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    private StatefulRedisPubSubConnection<String, String> publishConnection;
    private RedisPubSubAsyncCommands<String, String> asyncCommands;

    @PostConstruct
    public void init() {
        this.publishConnection = redisClient.connectPubSub();
        this.asyncCommands = publishConnection.async();
    }

    @PreDestroy
    public void shutdown() {
        if (publishConnection != null && publishConnection.isOpen()) {
            publishConnection.close();
        }
    }

    @Override
    public void publish(Long roomId, ChatMessageResponse response) {
        try {
            String payload = objectMapper.writeValueAsString(response);
            String channel = RedisConstants.CHAT_CHANNEL_PREFIX + roomId;
            asyncCommands.publish(channel, payload);
        } catch (Exception e) {
            throw new RedisException(RedisPubSubErrorCode.REDIS_PUBLISH_ERROR, e);
        }
    }
}
