package com.mokakbob.matching.service;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.cache.ParticipantStore;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private final ParticipantStore participantStore;
    private final ParticipantGeoStore geoStore;
    private final CategoryQueueStore queueStore;

    public void participateMatching(MatchingParticipateEvent event) {
        Long memberId = event.memberId();

        if (!queueStore.hasEnoughForMatching(event.category(), event.participantCount())) {
            return;
        }
    }
}
