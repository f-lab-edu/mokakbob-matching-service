package com.mokakbob.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.domain.chat.pubsub.ChatSubscriber;
import com.mokakbob.domain.chat.pubsub.response.ChatMessageResponse;
import com.mokakbob.exception.RedisPubSubErrorCode;
import com.mokakbob.metrix.ChatMetrics;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatSubscriber {

    private static final String CHANNEL_PREFIX = "chat.";
    private static final int ROOM_COUNT_FOR_TEST = 10;
    private static final String METRICS_EVENT = "chat_receive_message";

    private final RedisClusterClient redisClusterClient;
    private final ObjectMapper objectMapper;
    private final ChatSubscriber chatSubscriber;
    private final ChatMetrics chatMetrics;
    private final Set<String> subscribedChannels = ConcurrentHashMap.newKeySet();

    /**
     * Sharded Pub/Sub용 Pub/Sub connection.
     */
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private RedisPubSubAsyncCommands<String, String> asyncCommands;


    @PostConstruct
    public void init() {
        this.pubSubConnection = redisClusterClient.connectPubSub();
        this.asyncCommands = pubSubConnection.async();
        this.pubSubConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void smessage(String channel, String message) {
                handleIncomingMessage(channel, message);
            }
        });

        // 테스트용 방 10개(channel 10개)에 대해 SSUBSCRIBE
        for (int roomId = 0; roomId < ROOM_COUNT_FOR_TEST; roomId++) {
            String channel = CHANNEL_PREFIX + roomId;
            asyncCommands.ssubscribe(channel);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (pubSubConnection != null && pubSubConnection.isOpen()) {
            pubSubConnection.close();
        }
    }

    public void subscribeRoom(Long roomId) {
        String channel = CHANNEL_PREFIX + roomId;

        if (subscribedChannels.contains(channel)) {
            return;
        }

        asyncCommands.ssubscribe(channel);
        subscribedChannels.add(channel);
    }

    /**
     * SPUBLISH 로 발행된 메시지 처리.
     */
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
            throw new RedisException(RedisPubSubErrorCode.FAIL_REDIS_SUBSCRIBE);
        } finally {
            chatMetrics.recordLatency(METRICS_EVENT, System.currentTimeMillis() - start);
        }
    }
}
