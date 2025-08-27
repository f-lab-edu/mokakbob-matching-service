package com.mokakbob.pubsub.publisher;

import com.mokakbob.pubsub.RedisChannelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publish(RedisChannelType type, String message, Long memberId) {
        String channel = type.formatChannel(String.valueOf(memberId));
        redisTemplate.convertAndSend(channel, message);
        log.info("[Redis Pub/Sub] published channel={}, message={}", channel, message);
    }
}
