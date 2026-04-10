package com.mokakbob.matching.service;


import com.mokakbob.common.exception.ApiException;
import com.mokakbob.domain.matching.domain.vo.Location;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.service.MatchingService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.matching.exception.MatchingErrorCode;
import com.mokakbob.matching.ParticipantRedisStore;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingTransactionService {

    private static final int DEFAULT_DEDUCE_POINT = 2000;

    private final ParticipantRedisStore participantStore;
    private final MemberService memberService;
    private final MatchingService matchingService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void participateMatching(double lat, double lng, MatchingCategory category, int participantCount,
                                    Long memberId) {
        validateExistParticipating(memberId);
        deducePoint(memberId);

        Location location = Location.of(BigDecimal.valueOf(lat), BigDecimal.valueOf(lng));
        matchingService.saveMatchingRequest(memberId, category, participantCount, location);

        String idempotencyKey = generateIdempotencyKey(String.valueOf(memberId));
        MatchingParticipateEvent event = new MatchingParticipateEvent(idempotencyKey, memberId, location, category, participantCount);

        eventPublisher.publishEvent(event);
    }

    private String generateIdempotencyKey(String memberId) {
        long timestamp = System.currentTimeMillis();
        return memberId + "-" + timestamp;
    }

    private void deducePoint(Long memberId) {
        Member member = memberService.findMemberForUpdate(memberId);

        if(member.getDepositPoint() < DEFAULT_DEDUCE_POINT) {
            throw new ApiException(MatchingErrorCode.NOT_ENOUGH_MATCHING_POINT);
        }

        member.deductPoint(DEFAULT_DEDUCE_POINT);
    }

    private void validateExistParticipating(Long memberId) {
        if (participantStore.isAlreadyParticipating(memberId)) {
            throw new ApiException(MatchingErrorCode.EXIST_MATCHING);
        }
    }
}
