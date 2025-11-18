package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.domain.chat.pubsub.ChatPublisher;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.exception.RedisPubSubErrorCode;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatPublisher implements ChatPublisher {

    private static final String CHANNEL_PREFIX = "chat.";

    private final RedisClusterClient redisClusterClient;
    private final ObjectMapper objectMapper;

    /**
     * Sharded Pub/Sub용 Cluster connection.
     */
    private StatefulRedisClusterConnection<String, String> publishConnection;
    private RedisAdvancedClusterAsyncCommands<String, String> asyncCommands;

    @PostConstruct
    public void init() {
        this.publishConnection = redisClusterClient.connect();
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
            String channel = CHANNEL_PREFIX + roomId;

            asyncCommands.spublish(channel, payload)
                    .toCompletableFuture()
                    .join();
        } catch (Exception e) {
            throw new RedisException(RedisPubSubErrorCode.FAIL_REDIS_PUBLISH);
        }
    }
}
