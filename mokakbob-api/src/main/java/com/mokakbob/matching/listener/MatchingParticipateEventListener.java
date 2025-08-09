package com.mokakbob.matching.listener;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.cache.ParticipantStore;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MatchingParticipateEventListener {

    private final ParticipantStore participantStore;
    private final ParticipantGeoStore geoStore;
    private final CategoryQueueStore categoryQueueStore;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchingParticipateEvent(MatchingParticipateEvent event) {
        participantStore.transitionToParticipating(event.memberId());
        geoStore.addMemberLocation(event.memberId(), event.lng(), event.lat());
        categoryQueueStore.addToQueue(event.category(), event.participantCount(), event.memberId());
    }
}
