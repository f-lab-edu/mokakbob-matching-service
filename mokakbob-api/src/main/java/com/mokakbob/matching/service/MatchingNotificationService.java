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
