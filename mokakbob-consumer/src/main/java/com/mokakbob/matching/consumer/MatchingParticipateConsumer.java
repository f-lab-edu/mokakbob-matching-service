package com.mokakbob.matching.consumer;

import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import com.mokakbob.matching.service.MatchingParticipateApiService;
import com.mokakbob.topic.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingParticipateConsumer {

    private final MatchingParticipateApiService participateService;

    @KafkaListener(
            topics = KafkaTopic.MATCHING_PARTICIPATE,
            containerFactory = "matchingParticipateKafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, MatchingParticipateEvent> record, Acknowledgment ack) {
        final String key = record.key();
        final MatchingParticipateEvent event = record.value();

        try {
            participateService.lockParticipateMatching(event);
            ack.acknowledge();
            log.info("[OK] key={}, partition={}, offset={}", key, record.partition(), record.offset());
        } catch (Exception e) {
            log.error("[ERR] key={}, partition={}, offset={}, cause={}", key, record.partition(), record.offset(), e.toString());
            throw e;
        }
    }
}
