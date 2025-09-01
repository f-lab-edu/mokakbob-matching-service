package com.mokakbob.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.cache.NotificationStore;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.exception.NotificationErrorCode;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRedisStore implements NotificationStore {

    private static final String NOTIFICATION_KEY = "notification:";

    private final RedisTemplate<String, String> basicRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(Notification notification, long ttlSeconds) {
        try {
            String key = notificationKey(notification.getMemberId());
            String notificationId = String.valueOf(notification.getId());
            String value = objectMapper.writeValueAsString(notification);

            basicRedisTemplate.opsForHash()
                    .put(key, notificationId, value);

            basicRedisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            throw new RedisException(NotificationErrorCode.FAIL_REDIS_OPERATION);
        }
    }

    @Override
    public Notification findById(Long memberId, Long notificationId) {
        try {
            String key = notificationKey(memberId);
            String value = (String) basicRedisTemplate.opsForHash()
                    .get(key, String.valueOf(notificationId));

            if (value == null || value.isEmpty()) {
                throw new RedisException(NotificationErrorCode.NOT_FOUND_NOTIFICATION);
            }

            return objectMapper.readValue(value, Notification.class);
        } catch (Exception e) {
            throw new RedisException(NotificationErrorCode.FAIL_REDIS_OPERATION);
        }
    }

    @Override
    public void delete(Long memberId, Long notificationId) {
        basicRedisTemplate.opsForHash()
                .delete(notificationKey(memberId), String.valueOf(notificationId));
    }

    private String notificationKey(Long memberId) {
        return NOTIFICATION_KEY + memberId;
    }
}
