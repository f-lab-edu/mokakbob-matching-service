package com.mokakbob.chat.util;

import com.mokakbob.chat.controller.response.ChatParticipantResponse;
import com.mokakbob.chat.controller.response.ChatRoomResponse;
import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.matching.domain.Matching;
import com.mokakbob.domain.matching.domain.MatchingParticipant;
import com.mokakbob.domain.member.domain.Member;
import java.util.List;
import java.util.Map;

public final class ChatRoomMapper {

    private ChatRoomMapper() {
    }

    /**
     * ChatRoom -> ChatRoomResponse 변환
     */
    public static ChatRoomResponse toChatRoomResponse(
            ChatRoom room,
            List<MatchingParticipant> participants,
            Map<Long, Member> memberMap
    ) {
        Matching matching = room.getMatching();

        List<ChatParticipantResponse> participantResponses =
                toChatParticipantResponses(participants, memberMap);

        return ChatRoomResponse.of(
                room,
                matching,
                participantResponses
        );
    }

    /**
     * MatchingParticipant + Member -> ChatParticipantResponse 변환
     */
    public static List<ChatParticipantResponse> toChatParticipantResponses(
            List<MatchingParticipant> participants,
            Map<Long, Member> members
    ) {
        return participants.stream()
                .map(p -> ChatParticipantResponse.of(
                        p,
                        members.get(p.getMemberId())
                ))
                .toList();
    }
}
