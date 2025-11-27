package com.mokakbob.chat.controller.response;

import java.util.List;

public record ChatRoomResponses(
        Long memberId,
        List<ChatRoomResponse> rooms
) {

    public static ChatRoomResponses of(
            Long memberId,
            List<ChatRoomResponse> rooms
    ) {
        return new ChatRoomResponses(memberId, rooms);
    }
}
