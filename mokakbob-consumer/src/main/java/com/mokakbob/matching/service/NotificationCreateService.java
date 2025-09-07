package com.mokakbob.matching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.cache.NotificationStore;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.matching.common.exception.exceptions.ConsumerException;
import com.mokakbob.matching.exception.MatchingConsumerErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCreateService {

    private static final String MATCHING_TYPE = "MATCHING_FOUND";
    private static final int NOTIFICATION_EXPIRE_TIME_SECONDS = 300;

    private final NotificationStore notificationStore;
    private final ObjectMapper objectMapper;

    public void createNotification(MatchingFoundEvent event) {
        List<Notification> notifications = event.matched().stream()
                .map(memberId -> Notification.builder()
                        .memberId(memberId)
                        .key(event.key())
                        .type(MATCHING_TYPE)
                        .payload(buildPayload(event))
                        .build())
                .toList();

        notificationStore.save(event.key(), notifications, NOTIFICATION_EXPIRE_TIME_SECONDS);
    }

    private String buildPayload(MatchingFoundEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_NOTIFICATION_SERIALIZE_FAILED);
        }
    }
}
