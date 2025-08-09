package com.mokakbob.matching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.service.MatchingService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.domain.outbox.service.OutboxService;
import com.mokakbob.global.exception.GlobalErrorCode;
import com.mokakbob.matching.constant.KafkaTopics;
import com.mokakbob.matching.exception.MatchingErrorCode;
import com.mokakbob.matching.ParticipantRedisStore;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingStartService {

    private static final int DEFAULT_DEDUCE_POINT = 2000;

    private final ParticipantRedisStore participantStore;
    private final MemberService memberService;
    private final MatchingService matchingService;
    private final OutboxService outboxService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void participateMatching(double lat, double lng, MatchingCategory category, int participantCount,
                                    Long memberId) {
        validateExistParticipating(memberId);
        deducePoint(memberId);

        matchingService.saveMatchingRequest(memberId, category, participantCount, new BigDecimal(lat),
                new BigDecimal(lng));

        MatchingParticipateEvent event = new MatchingParticipateEvent(memberId, lat, lng, category, participantCount);
        saveOutbox(event);

        eventPublisher.publishEvent(event);
    }

    private void saveOutbox(MatchingParticipateEvent event) {
        try {
            String eventPayload = objectMapper.writeValueAsString(event);
            outboxService.saveOutbox(event.memberId(), event.getClass().getSimpleName(), eventPayload, KafkaTopics.MATCHING_PARTICIPATE);
        } catch (JsonProcessingException e) {
            throw new ApiException(GlobalErrorCode.JSON_SERIALIZATION);
        }
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
