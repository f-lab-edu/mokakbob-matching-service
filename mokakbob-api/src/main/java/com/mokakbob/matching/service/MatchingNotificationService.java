package com.mokakbob.matching.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.cache.NotificationStore;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.domain.matching.event.MatchingSuccessEvent;
import com.mokakbob.matching.exception.MatchingErrorCode;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


/**
 * 매칭 알림 응답 처리 서비스
 *
 * <p>주요 역할:</p>
 * <ul>
 *   <li>알림 응답 시 멱등성 키(idempotencyKey) 단위로 분산락을 잡아 동시성 제어</li>
 *   <li>모든 멤버가 응답 완료되면 매칭 성공/거절 이벤트를 발급</li>
 * </ul>
 *
 * <p>핵심 흐름:</p>
 * <ol>
 *   <li>{@link #lockNotificationRequest(Notification)}
 *       → 응답 처리 전 분산락을 획득하고 {@code handleNotification} 실행</li>
 *   <li>{@link #handleNotification(Notification)}
 *       → Room 단위 응답 집계, 전체 응답 완료 여부 및 수락/거절 상태 확인</li>
 *   <li>{@link #publishSuccessEvent(List)}
 *       → 전원 수락 시 {@code MatchingSuccessEvent} 발급</li>
 *   <li>{@link #publishRejectEvent(Notification)}
 *       → 거절 발생 시 {@code matching.reject} 이벤트 발급 예정</li>
 * </ol>
 *
 * <p>설계 포인트:</p>
 * <ul>
 *   <li>Redis TTL 기반으로 알림 만료 관리 (SSOT 보장)</li>
 *   <li>락: {@code idempotencyKey} 기준으로 획득 → 중복 처리 방지</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class MatchingNotificationService {

    private static final int LIMIT_LOCK_CATCH_TIME_SECONDS = 3;
    private static final int LOCK_DURATION_TIME_SECONDS = 5;

    private final RedissonClient redissonClient;
    private final NotificationStore notificationStore;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public void lockNotificationRequest(Notification notification) {
        String idempotencyKey = notification.getKey();
        RLock lock = redissonClient.getLock(idempotencyKey + ":lock");

        try {
            if (!lock.tryLock(LIMIT_LOCK_CATCH_TIME_SECONDS, LOCK_DURATION_TIME_SECONDS, TimeUnit.SECONDS)) {
                throw new ApiException(MatchingErrorCode.ALREADY_PROCESSING_NOTIFICATION);
            }

            handleNotification(notification);

        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
            throw new ApiException(MatchingErrorCode.ALREADY_PROCESSING_NOTIFICATION);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void handleNotification(Notification notification) {
        List<Notification> notifications = notificationStore.findAllByKey(notification.getKey());

        if (notifications.stream().anyMatch(n -> !n.isResponded())) {
            return;
        }

        if (notifications.stream().allMatch(n -> Boolean.TRUE.equals(n.getAccepted()))) {
            publishSuccessEvent(notifications);
        } else {
            publishRejectEvent(notification);
        }
    }

    private void publishSuccessEvent(List<Notification> notifications) {
        Notification notification = notifications.get(0);
        MatchingFoundEvent event = parseEvent(notification);

        MatchingSuccessEvent successEvent = new MatchingSuccessEvent(
                event.key(),
                event.category(),
                event.participantCount(),
                notifications.stream()
                        .map(Notification::getMemberId).toList(),
                Instant.now()
        );

        eventPublisher.publishEvent(successEvent);
    }

    private void publishRejectEvent(Notification notification) {
        // todo: matching.reject 토픽 발급 이벤트 publisher 구현
    }

    private MatchingFoundEvent parseEvent(Notification notification) {
        try {
            return objectMapper.readValue(notification.getPayload(), MatchingFoundEvent.class);
        } catch (IOException e) {
            throw new ApiException(MatchingErrorCode.FAIL_PARSE_NOTIFICATION);
        }
    }
}
