package com.mokakbob.matching.service;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.service.MatchingService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.matching.domain.CategoryQueueStore;
import com.mokakbob.matching.domain.ParticipantGeoStore;
import com.mokakbob.matching.exception.MatchingErrorCode;
import com.mokakbob.matching.infrastructure.ParticipantRedisStore;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingStartService {

    private static final int DEFAULT_DEDUCE_POINT = 2000;

    private final ParticipantRedisStore participantStore;
    private final ParticipantGeoStore geoStore;
    private final MemberService memberService;
    private final MatchingService matchingService;
    private final CategoryQueueStore categoryQueueStore;

    public void startMatching(double lat, double lng, MatchingCategory category, int participantCount, Long memberId) {
        validateExistParticipating(memberId);
        deducePoint(memberId);

        matchingService.saveMatchingRequest(
                memberId,
                category,
                participantCount,
                new BigDecimal(lat),
                new BigDecimal(lng)
        );

        geoStore.addUserLocation(memberId, lng, lat);
        participantStore.transitionToParticipating(memberId);
        categoryQueueStore.addToQueue(category, participantCount, memberId);
    }

    private void deducePoint(Long memberId) {
        Member member = memberService.findMember(memberId);
        member.deductPoint(DEFAULT_DEDUCE_POINT);
    }

    private void validateExistParticipating(Long memberId) {
        if(participantStore.isAlreadyParticipating(memberId)) {
            throw new ApiException(MatchingErrorCode.EXIST_MATCHING);
        }
    }
}
