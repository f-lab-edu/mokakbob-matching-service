package com.mokakbob.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.cache.NotificationStore;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.config.RedisConstants;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.common.exception.RedisStoreErrorCode;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRedisStore implements NotificationStore {

    private final RedisTemplate<String, String> basicRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(String key, List<Notification> notifications, long ttlSeconds) {
        Objects.requireNonNull(key, RedisConstants.ERR_KEY_NULL);
        String roomKey = RedisConstants.NOTIFICATION_ROOM_PREFIX + key;

        try {
            for (Notification notification : notifications) {
                Objects.requireNonNull(notification, RedisConstants.ERR_NOTIFICATION_NULL);
                String value = objectMapper.writeValueAsString(notification);

                basicRedisTemplate.opsForHash()
                        .put(
                                roomKey,
                                String.valueOf(notification.getMemberId()),
                                value
                        );

                basicRedisTemplate.opsForValue()
                        .set(
                                RedisConstants.NOTIFICATION_MEMBER_PREFIX + notification.getMemberId(),
                                roomKey
                        );
            }

            basicRedisTemplate.expire(roomKey, Duration.ofSeconds(ttlSeconds));
        } catch (IOException e) {
            throw new RedisException(RedisStoreErrorCode.REDIS_STORE_ERROR, e);
        }
    }

    @Override
    public Optional<Notification> findByMemberId(Long memberId) {
        Objects.requireNonNull(memberId, RedisConstants.ERR_MEMBER_ID_NULL);
        try {
            String roomId = basicRedisTemplate.opsForValue()
                    .get(RedisConstants.NOTIFICATION_MEMBER_PREFIX + memberId);

            if (roomId == null) {
                return Optional.empty();
            }

            Object valueObj = basicRedisTemplate.opsForHash()
                    .get(roomId, String.valueOf(memberId));
            
            if (valueObj == null) {
                return Optional.empty();
            }

            String value = (String) valueObj;
            return Optional.ofNullable(objectMapper.readValue(value, Notification.class));
        } catch (IOException e) {
            throw new RedisException(RedisStoreErrorCode.REDIS_STORE_ERROR, e);
        }
    }
}
