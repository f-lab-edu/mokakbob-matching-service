package com.mokakbob.matching.service;

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

/**
 * 매칭 참여 이벤트를 처리하는 서비스.
 * <p>
 * 동작 시나리오:
 * <ol>
 *   <li>기준 멤버(매칭 요청한 유저)를 예약 상태로 전환</li>
 *   <li>기준 멤버의 위치 정보를 기준으로 주변 후보자를 탐색</li>
 *   <li>후보자들을 순차적으로 예약 시도</li>
 *   <li>필요한 인원수가 모이지 않으면 예약한 멤버들을 모두 롤백</li>
 *   <li>충분한 인원이 모이면 최종 매칭 그룹 확정 후 상태 전환 및 이벤트 발행</li>
 * </ol>
 *
 * 멱등성을 보장하기 위해 reserveId(=idempotencyKey)를 사용하여
 * 동일한 Kafka 메시지 재처리 시 중복 매칭을 방지한다.
 */
@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private static final double RADIUS_METERS = 1500.0;
    private static final int MATCHING_ACCEPT_EXPIRE_TIME_SECONDS = 180;

    private final ParticipantGeoStore geoStore;
    private final ParticipantStore participantStore;
    private final MatchFoundEventPublisher publisher;


    /**
     * 매칭 참여 이벤트를 처리한다.
     *
     * @param event 매칭 참여 이벤트 (카테고리, 인원수, 멤버 정보 포함)
     * @throws ConsumerException 매칭 처리 중 예외 발생 시
     */
    public void participateMatching(MatchingParticipateEvent event) {
        MatchingCategory category = event.category();
        int participantCount = event.participantCount();
        String idempotencyKey = event.idempotencyKey();
        Long memberId = event.memberId();

        try {
            double[] location = findMemberDelimiterPlace(category, participantCount, memberId);

            boolean reservedMember = geoStore.reserveMember(category, participantCount, memberId, idempotencyKey, Duration.ofSeconds(10));
            if (!reservedMember) {
                return; // 이미 다른 매칭에서 처리된 경우
            }

            List<Long> candidates = findAndReserveCandidates(event, memberId, location, idempotencyKey);

            if (candidates.size() < participantCount - 1) {
                rollback(idempotencyKey, event);
                return;
            }

            List<Long> matched = buildMatchedGroup(memberId, candidates, event.participantCount());
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
        geoStore.rollbackReservation(reserveId, event.category(), event.participantCount());
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
                event.participantCount() - 1,
                location[1], // lng
                location[0], // lat
                RADIUS_METERS
        );

        List<Long> reserved = new ArrayList<>();

        for (Long candidateId : nearby) {
            if (candidateId.equals(memberId)) {
                continue;
            }
            boolean success = geoStore.reserveMember(
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

    private double[] findMemberDelimiterPlace(MatchingCategory category, int count, Long memberId) {
        return geoStore.getLocation(category, count, memberId)
                .orElseThrow(() -> new ConsumerException(MatchingConsumerErrorCode.NOT_FOUND_LOCATION));
    }
}
