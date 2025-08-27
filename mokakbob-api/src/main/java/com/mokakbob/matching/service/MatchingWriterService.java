package com.mokakbob.matching.service;

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

    @RedisRetryable
    public void saveParticipantInfo(MatchingParticipateEvent event) {
        participantStore.transitionToParticipating(event.memberId());
    }

    @RedisRetryable
    public void saveGeo(MatchingParticipateEvent event) {
        geoStore.addMemberLocation(event.category(), event.participantCount(), event.memberId(), event.lng(), event.lat());
    }
}
