package com.mokakbob.matching.service;

import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.cache.ParticipantStore;
import com.mokakbob.domain.matching.domain.vo.Location;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import com.mokakbob.matching.common.exception.ConsumerException;
import com.mokakbob.matching.event.MatchFoundEventPublisher;
import com.mokakbob.matching.exception.MatchingConsumerErrorCode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * 매칭 참여 이벤트를 처리하는 서비스.
 * <p>
 * 동작 시나리오:
 * <ol>
 *   <li>category + participant 대기열 락 획득</>
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
public class MatchingParticipateApiService {

    private static final double RADIUS_METERS = 1500.0;
    private static final int LIMIT_LOCK_CATCH_TIME = 3;
    private static final int LOCK_DURATION_TIME = 10;
    private static final String LOCK_KEY_PREFIX = "lock:matching:";

    private final ParticipantGeoStore geoStore;
    private final ParticipantStore participantStore;
    private final MatchFoundEventPublisher publisher;
    private final RedissonClient redissonClient;

    /**
     * category + participantCount 단위로 락을 잡고 participateMatching 실행
     */
    public void lockParticipateMatching(MatchingParticipateEvent event) {
        String lockKey = LOCK_KEY_PREFIX + event.category().name() + ":" + event.participantCount();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LIMIT_LOCK_CATCH_TIME, LOCK_DURATION_TIME, TimeUnit.SECONDS)) {
                participateMatching(event);
            } else {
                throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_LOCK_ACQUIRE_FAILED);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_LOCK_INTERRUPTED, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

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
            Location location = findMemberDelimiterPlace(category, participantCount, memberId);

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

            String matchKey = category.name() + "_" + participantCount + "_" + System.currentTimeMillis();

            MatchingFoundEvent matchingFoundEvent = new MatchingFoundEvent(
                    matchKey,
                    category,
                    participantCount,
                    matched
            );
            publisher.publishFound(matchingFoundEvent);

        } catch (Exception e) {
            rollback(idempotencyKey, event);
            throw new ConsumerException(MatchingConsumerErrorCode.MATCHING_PARTICIPATE_CONSUMER_EXCEPTION, e);
        }
    }

    private List<Long> buildMatchedGroup(Long delimiterMemberId, List<Long> candidates, int participantCount) {
        List<Long> matched = new ArrayList<>();
        matched.add(delimiterMemberId);
        matched.addAll(candidates.subList(0, Math.min(participantCount - 1, candidates.size())));
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
    private List<Long> findAndReserveCandidates(MatchingParticipateEvent event, Long memberId, Location location, String reserveId) {
        List<Long> nearby = geoStore.findNearbyMembers(
                event.category(),
                event.participantCount(),
                location,
                RADIUS_METERS
        );

        List<Long> reserved = new ArrayList<>();
        int requiredCount = event.participantCount() - 1;

        for (Long candidateId : nearby) {
            if (candidateId.equals(memberId)) {
                continue;
            }

            if (reserved.size() >= requiredCount) {
                break;
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

    private Location findMemberDelimiterPlace(MatchingCategory category, int count, Long memberId) {
        return geoStore.getLocation(category, count, memberId)
                .orElseThrow(() -> new ConsumerException(MatchingConsumerErrorCode.NOT_FOUND_LOCATION));
    }
}
