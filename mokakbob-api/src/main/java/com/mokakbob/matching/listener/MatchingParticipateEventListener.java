package com.mokakbob.matching.listener;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.cache.ParticipantStore;
import com.mokakbob.domain.log.domain.vo.KafkaTopic;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchingParticipateEventListener {

    private final ParticipantStore participantStore;
    private final ParticipantGeoStore geoStore;
    private final CategoryQueueStore categoryQueueStore;
    private final KafkaTemplate<String, MatchingParticipateEvent> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchingParticipateEvent(MatchingParticipateEvent event) {
        sendKafkaTopic(event);

        participantStore.transitionToParticipating(event.memberId());
        geoStore.addMemberLocation(event.memberId(), event.lng(), event.lat());
        categoryQueueStore.addToQueue(event.category(), event.participantCount(), event.memberId());
    }

    private void sendKafkaTopic(MatchingParticipateEvent event) {
        try {
            String key = String.valueOf(event.memberId());
            kafkaTemplate.send(KafkaTopic.MATCHING_PARTICIPATE.getTopicName(), key, event);
        } catch (Exception e) {
            log.error("카프카 토픽 발급 실패: {}", e.getMessage());
            log.error("실패한 이벤트: {}", event);

            log.error("재처리 필요 정보 - memberId: {}, key: {}, lat: {}, lng: {}, category: {}, participantCount: {}",
                    event.memberId(), event.memberId(), event.lat(), event.lng(), event.category(), event.participantCount());
        }
    }
}
