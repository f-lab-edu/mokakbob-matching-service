package com.mokakbob.chat.controller.response;

import com.mokakbob.domain.chat.domain.ChatRoom;
import com.mokakbob.domain.matching.domain.Matching;
import com.mokakbob.domain.matching.domain.vo.MatchingStatus;
import java.util.List;

public record ChatRoomResponse(
        Long roomId,
        Long matchingId,
        MatchingStatus matchingStatus,
        List<ChatParticipantResponse> participants
) {

    public static ChatRoomResponse of(
            ChatRoom room,
            Matching matching,
            List<ChatParticipantResponse> participants
            ) {
        return new ChatRoomResponse(
                room.getId(),
                matching.getId(),
                matching.getStatus(),
                participants
        );
    }
}
