package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.config.RedisConstants;
import com.mokakbob.domain.chat.pubsub.ChatSubscriber;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.common.exception.RedisPubSubErrorCode;
import com.mokakbob.metrix.ChatMetrics;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisChatSubscriber {

    private static final int ROOM_COUNT_FOR_TEST = 10;
    private static final String METRICS_EVENT = "chat_receive_message";

    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;
    private final ChatSubscriber chatSubscriber;
    private final ChatMetrics chatMetrics;
    private final Set<String> subscribedChannels = ConcurrentHashMap.newKeySet();

    private StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private RedisPubSubAsyncCommands<String, String> asyncCommands;

    @PostConstruct
    public void init() {
        this.pubSubConnection = redisClient.connectPubSub();
        this.asyncCommands = pubSubConnection.async();
        this.pubSubConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                handleIncomingMessage(channel, message);
            }
        });

        for (int roomId = 0; roomId < ROOM_COUNT_FOR_TEST; roomId++) {
            subscribeRoom((long) roomId);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (pubSubConnection != null && pubSubConnection.isOpen()) {
            pubSubConnection.close();
        }
    }

    public void subscribeRoom(Long roomId) {
        String channel = RedisConstants.CHAT_CHANNEL_PREFIX + roomId;
        if (subscribedChannels.add(channel)) {
            doSubscribe(channel);
        }
    }

    private void doSubscribe(String channel) {
        asyncCommands.subscribe(channel);
    }

    private void handleIncomingMessage(String channel, String rawBody) {
        long start = System.currentTimeMillis();

        try {
            ChatMessageResponse payload =
                    objectMapper.readValue(rawBody.getBytes(StandardCharsets.UTF_8), ChatMessageResponse.class);

            long e2e = System.currentTimeMillis() - payload.sentAt();
            chatMetrics.countReceive(METRICS_EVENT);
            chatMetrics.recordEndToEnd(e2e);

            chatSubscriber.handleMessage(channel, payload);
        } catch (Exception e) {
            chatMetrics.countError(METRICS_EVENT);
            throw new RedisException(RedisPubSubErrorCode.REDIS_SUBSCRIBE_ERROR, e);
        } finally {
            chatMetrics.recordLatency(METRICS_EVENT, System.currentTimeMillis() - start);
        }
    }
}
