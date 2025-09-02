package com.mokakbob.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.cache.NotificationStore;
import com.mokakbob.common.exception.RedisException;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.exception.NotificationErrorCode;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRedisStore implements NotificationStore {

    private static final String ROOM_KEY_PREFIX = "notification:room:";
    private static final String MEMBER_KEY_PREFIX = "notification:member:";

    private final RedisTemplate<String, String> basicRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 매칭방 단위로 알림을 저장한다.
     * <p>
     * Redis 구조: - roomKey(Hash) (방 TTL 적용) - memberKey(String) (방 TTL과 동일)
     *
     * @param key           매칭방 식별자(멱등성 키)
     * @param notifications 회원별 알림 리스트
     * @param ttlSeconds    방 TTL(초)
     */
    @Override
    public void save(String key, List<Notification> notifications, long ttlSeconds) {
        String roomKey = ROOM_KEY_PREFIX + key;

        try {
            for (Notification notification : notifications) {
                String value = objectMapper.writeValueAsString(notification);

                basicRedisTemplate.opsForHash()
                        .put(
                                roomKey,
                                String.valueOf(notification.getMemberId()),
                                value
                        );

                basicRedisTemplate.opsForValue()
                        .set(
                                MEMBER_KEY_PREFIX + notification.getMemberId(),
                                roomKey
                        );
            }

            basicRedisTemplate.expire(roomKey, Duration.ofSeconds(ttlSeconds));
        } catch (IOException e) {
            throw new RedisException(NotificationErrorCode.FAIL_REDIS_OPERATION);
        }
    }

    /**
     * memberId로 해당 회원의 알림을 조회한다.
     * <p>
     * - memberId → roomId 매핑 조회
     * - roomId 해시에서 memberId 알림 JSON 조회
     *
     * @param memberId 회원 ID
     * @return Notification 객체
     * @throws RedisException 알림이 없거나 조회 실패 시
     */
    @Override
    public Optional<Notification> findByMemberId(Long memberId) {
        try {
            String roomId = basicRedisTemplate.opsForValue()
                    .get(MEMBER_KEY_PREFIX + memberId);

            if (roomId == null) {
                return Optional.empty();
            }

            String value = (String) basicRedisTemplate.opsForHash()
                    .get(roomId, String.valueOf(memberId));

            if (value == null) {
                return Optional.empty();
            }

            return Optional.ofNullable(objectMapper.readValue(value, Notification.class));
        } catch (IOException e) {
            throw new RedisException(NotificationErrorCode.FAIL_REDIS_OPERATION);
        }
    }
}
