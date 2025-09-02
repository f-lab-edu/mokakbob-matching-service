package com.mokakbob.matching.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.service.MatchingService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.matching.ParticipantRedisStore;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class MatchingTransactionServiceTest {

    @InjectMocks
    private MatchingTransactionService matchingTransactionService;

    @Mock
    private ParticipantRedisStore participantStore;

    @Mock
    private MemberService memberService;

    @Mock
    private MatchingService matchingService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void 매칭_참여_정상_흐름_테스트() {
        // given
        Long memberId = 1L;
        double lat = 37.5;
        double lng = 127.0;
        MatchingCategory category = MatchingCategory.CHINESE;
        int participantCount = 2;
        Member fakeMember = Member.builder()
                .depositPoint(5000)
                .build();

        given(participantStore.isAlreadyParticipating(memberId)).willReturn(false);
        given(memberService.findMemberForUpdate(memberId)).willReturn(fakeMember);

        // when
        matchingTransactionService.participateMatching(lat, lng, category, participantCount, memberId);

        // then
        verify(participantStore).isAlreadyParticipating(memberId);
        verify(memberService).findMemberForUpdate(memberId);
        verify(matchingService).saveMatchingRequest(eq(memberId), eq(category), eq(participantCount), any());
        verify(eventPublisher).publishEvent(any(MatchingParticipateEvent.class));
    }
}
