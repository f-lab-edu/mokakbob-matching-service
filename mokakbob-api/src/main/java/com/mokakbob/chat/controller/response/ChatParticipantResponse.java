package com.mokakbob.chat.controller.response;

import com.mokakbob.domain.matching.domain.MatchingParticipant;
import com.mokakbob.domain.member.domain.Member;

public record ChatParticipantResponse(
        Long memberId,
        String nickname,
        String profileImageUrl
) {

    public static ChatParticipantResponse of(
            MatchingParticipant participant,
            Member member
    ) {
        return new ChatParticipantResponse(
                participant.getMemberId(),
                member.getNickname(),
                member.getProfileImage()
        );
    }
}
