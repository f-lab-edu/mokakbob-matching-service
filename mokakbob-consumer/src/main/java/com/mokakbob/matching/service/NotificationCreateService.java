package com.mokakbob.matching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.cache.NotificationStore;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.matching.common.exception.exceptions.ConsumerException;
import com.mokakbob.matching.exception.MatchingConsumerErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCreateService {

    private static final long NOTIFICATION_TTL_SECONDS = 20L;

    private final NotificationStore notificationStore;
    private final ObjectMapper objectMapper;

    public void createNotification(MatchingFoundEvent event) {
        event.matched().forEach(memberId -> {
            Notification notification = Notification.builder()
                    .id(generateId())
                    .memberId(memberId)
                    .type("MATCHING_FOUND")
                    .payload(buildPayload(event))
                    .build();

            notificationStore.save(notification, NOTIFICATION_TTL_SECONDS);

            log.info("알림 생성 완료 - memberId: {}, notificationId: {}",
                    memberId, notification.getId());
        });
    }

    private String buildPayload(MatchingFoundEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_NOTIFICATION_SERIALIZE_FAILED);
        }
    }


    private long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
