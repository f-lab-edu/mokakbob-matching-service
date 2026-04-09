package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.config.RedisConstants;
import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.common.exception.RedisPubSubErrorCode;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatPublisher implements ChatPublisher {

    private final io.lettuce.core.AbstractRedisClient redisClient;
    private final ObjectMapper objectMapper;

    private StatefulRedisPubSubConnection<String, String> publishConnection;
    private RedisPubSubAsyncCommands<String, String> asyncCommands;
    private boolean isClusterMode;

    @PostConstruct
    public void init() {
        this.isClusterMode = redisClient instanceof RedisClusterClient;
        
        if (isClusterMode) {
            this.publishConnection = ((RedisClusterClient) redisClient).connectPubSub();
        } else {
            this.publishConnection = ((io.lettuce.core.RedisClient) redisClient).connectPubSub();
        }
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

            if (isClusterMode) {
                asyncCommands.spublish(channel, payload);
            } else {
                asyncCommands.publish(channel, payload);
            }
        } catch (Exception e) {
            throw new RedisException(RedisPubSubErrorCode.REDIS_PUBLISH_ERROR);
        }
    }
}
