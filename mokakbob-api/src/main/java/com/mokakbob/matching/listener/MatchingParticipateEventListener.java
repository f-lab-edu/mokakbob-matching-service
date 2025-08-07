package com.mokakbob.matching.listener;

import com.mokakbob.matching.constant.KafkaTopics;
import com.mokakbob.matching.domain.CategoryQueueStore;
import com.mokakbob.matching.domain.ParticipantGeoStore;
import com.mokakbob.matching.domain.ParticipantStore;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MatchingParticipateEventListener {

    private final KafkaTemplate<String, MatchingParticipateEvent> kafkaTemplate;
    private final ParticipantStore participantStore;
    private final ParticipantGeoStore geoStore;
    private final CategoryQueueStore categoryQueueStore;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchingParticipateEvent(MatchingParticipateEvent event) {
        kafkaTemplate.send(KafkaTopics.MATCHING_PARTICIPATE, event);

        participantStore.transitionToParticipating(event.memberId());
        geoStore.addMemberLocation(event.memberId(), event.lng(), event.lat());
        categoryQueueStore.addToQueue(event.category(), event.participantCount(), event.memberId());
    }
}
