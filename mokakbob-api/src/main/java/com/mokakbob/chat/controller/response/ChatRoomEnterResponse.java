package com.mokakbob.chat.controller.response;

public record ChatRoomEnterResponse(
        Long roomId,
        String wsUrl,
        String pub,
        String sub
) {
}
