package com.mokakbob.chat.controller.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record ChatRoomResponses(
        Long memberId,
        List<ChatRoomResponse> rooms,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static ChatRoomResponses of(
            Long memberId,
            Page<ChatRoomResponse> pageResult
    ) {
        return new ChatRoomResponses(
                memberId,
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }
}
