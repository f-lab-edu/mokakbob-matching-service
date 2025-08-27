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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private static final double RADIUS_METERS = 1500.0;
    private static final int MATCHING_ACCEPT_EXPIRE_TIME_SECONDS = 180;

    private final CategoryQueueStore queueStore;
    private final ParticipantGeoStore geoStore;
    private final ParticipantStore participantStore;
    private final MatchFoundEventPublisher publisher;


    /**
     * 매칭 참여 이벤트를 처리한다.
     * <p>
     * 1. 큐에서 충분한 인원이 있는지 확인
     * 2. 가장 오래된 멤버를 예약 (delimiter로 사용)
     * 3. delimiter 기준으로 주변 후보자를 탐색 및 예약
     * 4. 인원이 부족하면 롤백 후 종료
     * 5. 매칭 그룹 확정 후 상태 전환, 이벤트 발행
     *
     * @param event 매칭 참여 이벤트 (카테고리, 인원수, 멤버 정보 포함)
     * @throws ConsumerException 매칭 처리 중 예외 발생 시
     */
    public void participateMatching(MatchingParticipateEvent event) {
        MatchingCategory category = event.category();
        int participantCount = event.participantCount();
        String idempotencyKey = event.idempotencyKey();

        if(!queueStore.hasEnoughForMatching(category, participantCount)) {
            return;
        }

        try {
            List<Long> reservedMember = reserveMember(event, idempotencyKey);
            if (reservedMember.isEmpty()) {
                return;
            }

            Long delimiterMemberId = reservedMember.get(0);
            double[] location = findMemberDelimiterPlace(category, participantCount, delimiterMemberId);
            List<Long> candidates = findAndReserveCandidates(event, delimiterMemberId, location, idempotencyKey);

            if (candidates.size() < participantCount - 1) {
                rollback(idempotencyKey, event);
                return;
            }

            List<Long> matched = buildMatchedGroup(delimiterMemberId, candidates, event.participantCount());
            matched.forEach(participantStore::transitionToFound);

            MatchingFoundEvent matchingFoundEvent = new MatchingFoundEvent(
                    idempotencyKey,
                    category,
                    participantCount,
                    matched,
                    Instant.now().plusSeconds(MATCHING_ACCEPT_EXPIRE_TIME_SECONDS)
            );
            publisher.publishFound(matchingFoundEvent);

        } catch (Exception e) {
            rollback(idempotencyKey, event);
            throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_PARTICIPATE_CONSUMER_EXCEPTION);
        }
    }

    private List<Long> buildMatchedGroup(Long delimiterMemberId, List<Long> candidates, int participantCount) {
        List<Long> matched = new ArrayList<>();
        matched.add(delimiterMemberId);
        matched.addAll(candidates.subList(0, participantCount - 1));
        return matched;
    }

    private void rollback(String reserveId, MatchingParticipateEvent event) {
        queueStore.rollbackReservation(reserveId, event.category(), event.participantCount());
    }

    /**
     * 기준 멤버(delimiter) 위치를 기반으로 근처 후보자를 찾고 예약한다.
     *
     * @param event     매칭 이벤트
     * @param memberId  기준 멤버 ID
     * @param location  기준 멤버의 위치 [lat, lng]
     * @param reserveId 예약 식별자
     * @return 예약에 성공한 후보자 ID 리스트
     */
    private List<Long> findAndReserveCandidates(MatchingParticipateEvent event, Long memberId, double[] location, String reserveId) {
        List<Long> nearby = geoStore.findNearbyMembers(
                event.category(),
                event.participantCount(),
                location[1], // lng
                location[0], // lat
                RADIUS_METERS
        );

        List<Long> reserved = new ArrayList<>();

        for (Long candidateId : nearby) {
            if (candidateId.equals(memberId)) {
                continue;
            }
            boolean success = queueStore.reserveSpecificMember(
                    event.category(),
                    event.participantCount(),
                    candidateId,
                    reserveId,
                    Duration.ofSeconds(10)
            );
            if (success) {
                reserved.add(candidateId);
            }
        }

        return reserved;
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
