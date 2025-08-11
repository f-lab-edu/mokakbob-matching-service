package com.mokakbob.matching.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mokakbob.topic.KafkaTopic;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.service.MatchingService;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.matching.ParticipantRedisStore;
import com.mokakbob.matching.service.event.MatchingParticipateEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class MatchingStartServiceTest {

    @InjectMocks
    private MatchingStartService matchingStartService;

    @Mock
    private ParticipantRedisStore participantStore;

    @Mock
    private ParticipantGeoStore geoStore;

    @Mock
    private MemberService memberService;

    @Mock
    private MatchingService matchingService;

    @Mock
    private CategoryQueueStore categoryQueueStore;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

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
        given(memberService.findMember(memberId)).willReturn(fakeMember);

        // when
        matchingStartService.participateMatching(lat, lng, category, participantCount, memberId);

        // then
        verify(participantStore).isAlreadyParticipating(memberId);
        verify(memberService).findMember(memberId);
        verify(matchingService).saveMatchingRequest(eq(memberId), eq(category), eq(participantCount), any(), any());
        verify(geoStore).addMemberLocation(memberId, lng, lat);
        verify(participantStore).transitionToParticipating(memberId);
        verify(categoryQueueStore).addToQueue(category, participantCount, memberId);
        verify(kafkaTemplate).send(eq(KafkaTopic.MATCHING_PARTICIPATE.getTopicName()), any(MatchingParticipateEvent.class));
    }
}
