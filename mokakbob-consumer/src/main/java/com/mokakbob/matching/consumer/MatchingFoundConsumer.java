package com.mokakbob.matching.consumer;

import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.matching.service.NotificationCreateService;
import com.mokakbob.topic.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingFoundConsumer {

    private final NotificationCreateService notificationCreateService;

    @KafkaListener(
            topics = KafkaTopic.MATCHING_FOUND,
            containerFactory = "matchingFoundKafkaListenerContainerFactory"
    )
    public void consume(MatchingFoundEvent event) {
        notificationCreateService.createNotification(event);
    }
}
