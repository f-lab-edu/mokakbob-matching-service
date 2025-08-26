package com.mokakbob.matching.service;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.cache.ParticipantStore;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import com.mokakbob.matching.common.exception.exceptions.ConsumerException;
import com.mokakbob.matching.event.MatchFoundEventPublisher;
import com.mokakbob.matching.exception.MatchingConsumerErrorCode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private static final double RADIUS_METERS = 1500.0;
    private static final int MATCHING_ACCEPT_EXPIRE_TIME = 180;

    private final CategoryQueueStore queueStore;
    private final ParticipantGeoStore geoStore;
    private final ParticipantStore participantStore;
    private final MatchFoundEventPublisher publisher;

    public void participateMatching(MatchingParticipateEvent event) {
        MatchingCategory category = event.category();
        int participantCount = event.participantCount();
        String reserveId = UUID.randomUUID()
                .toString();

        if(!queueStore.hasEnoughForMatching(category, participantCount)) {
            return;
        }

        try {
            List<Long> reservedMember = reserveMember(event, reserveId);
            if (reservedMember.isEmpty()) {
                return;
            }

            Long delimiterMemberId = reservedMember.get(0);
            double[] location = findMemberDelimiterPlace(category, participantCount, delimiterMemberId);
            List<Long> candidates = findCandidates(event, delimiterMemberId, location);

            if (candidates.size() < participantCount - 1) {
                rollback(reserveId, event);
                return;
            }

            List<Long> matched = buildMatchedGroup(delimiterMemberId, candidates, event.participantCount());
            matched.forEach(participantStore::transitionToFound);

            MatchingFoundEvent matchingFoundEvent = new MatchingFoundEvent(
                    UUID.randomUUID().toString(),
                    category,
                    participantCount,
                    matched,
                    Instant.now().plusSeconds(MATCHING_ACCEPT_EXPIRE_TIME)
            );
            publisher.publishFound(matchingFoundEvent);

        } catch (Exception e) {
            rollback(reserveId, event);
            throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_PARTICIPATE_CONSUMER_EXCEPTION);
        }
    }

    private List<Long> buildMatchedGroup(Long pivotId, List<Long> candidates, int participantCount) {
        List<Long> matched = new ArrayList<>();
        matched.add(pivotId);
        matched.addAll(candidates.subList(0, participantCount - 1));
        return matched;
    }

    private void rollback(String reserveId, MatchingParticipateEvent event) {
        queueStore.rollbackReservation(reserveId, event.category(), event.participantCount());
    }

    private List<Long> findCandidates(MatchingParticipateEvent event, Long memberId, double[] location) {
        List<Long> nearby = geoStore.findNearbyMembers(
                event.category(),
                event.participantCount(),
                location[1], // lng
                location[0], // lat
                RADIUS_METERS
        );

        return nearby.stream()
                .filter(id -> !id.equals(memberId))
                .toList();
    }

    private List<Long> reserveMember(MatchingParticipateEvent event, String reserveId) {
        return queueStore.reserveOldestMember(
                event.category(),
                event.participantCount(),
                reserveId,
                Duration.ofSeconds(10)
        );
    }

    private double[] findMemberDelimiterPlace(MatchingCategory category, int count, Long memberId) {
        return geoStore.getLocation(category, count, memberId)
                .orElseThrow(() -> new ConsumerException(MatchingConsumerErrorCode.NOT_FOUND_LOCATION));
    }
}
