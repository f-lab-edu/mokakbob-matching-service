package com.mokakbob.matching.service;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.cache.ParticipantStore;
import com.mokakbob.matching.annotation.RedisRetryable;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingWriterService {

    private final ParticipantStore participantStore;
    private final ParticipantGeoStore geoStore;
    private final CategoryQueueStore categoryQueueStore;

    @RedisRetryable
    public void saveParticipantInfo(MatchingParticipateEvent event) {
        participantStore.transitionToParticipating(event.memberId());
    }

    @RedisRetryable
    public void saveGeo(MatchingParticipateEvent event) {
        geoStore.addMemberLocation(event.memberId(), event.lng(), event.lat());
    }

    @RedisRetryable
    public void saveQueue(MatchingParticipateEvent event) {
        categoryQueueStore.addToQueue(event.category(), event.participantCount(), event.memberId());
    }
}
