package com.mokakbob.matching.service;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.service.MatchingService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.matching.exception.MatchingErrorCode;
import com.mokakbob.matching.ParticipantRedisStore;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingStartService {

    private static final int DEFAULT_DEDUCE_POINT = 2000;
    private static final int LIMIT_LOCK_CATCH_TIME = 3;
    private static final int LOCK_DURATION_TIME = 10;

    private final ParticipantRedisStore participantStore;
    private final MemberService memberService;
    private final MatchingService matchingService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    @Transactional
    public void participateMatchingWithLock(double lat, double lng, MatchingCategory category, int participantCount,
                                            Long memberId) {
        String lockKey = "lock:member:" + memberId + ":participation";
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LIMIT_LOCK_CATCH_TIME, LOCK_DURATION_TIME, TimeUnit.SECONDS)) {
                participateMatching(lat, lng, category, participantCount, memberId);
            } else {
                throw new ApiException(MatchingErrorCode.EXIST_MATCHING);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(MatchingErrorCode.MATCHING_OPERATION_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void participateMatching(double lat, double lng, MatchingCategory category, int participantCount,
                                     Long memberId) {
        validateExistParticipating(memberId);
        deducePoint(memberId);

        matchingService.saveMatchingRequest(memberId, category, participantCount, new BigDecimal(lat),
                new BigDecimal(lng));

        MatchingParticipateEvent event = new MatchingParticipateEvent(memberId, lat, lng, category, participantCount);

        eventPublisher.publishEvent(event);
    }


    private void deducePoint(Long memberId) {
        Member member = memberService.findMember(memberId);
        member.deductPoint(DEFAULT_DEDUCE_POINT);
    }

    private void validateExistParticipating(Long memberId) {
        if (participantStore.isAlreadyParticipating(memberId)) {
            throw new ApiException(MatchingErrorCode.EXIST_MATCHING);
        }
    }
}
