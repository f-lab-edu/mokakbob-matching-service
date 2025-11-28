package com.mokakbob.chat.controller.response;

import java.util.List;
import org.springframework.data.domain.Pageable;

public record ChatRoomResponses(
        Long memberId,
        List<ChatRoomResponse> rooms,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {

    private static final Long EMPTY_TOTAL_ELEMENTS = 0L;
    private static final int EMPTY_TOTAL_PAGE = 0;

    public static ChatRoomResponses of(
            Long memberId,
            List<ChatRoomResponse> rooms,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean last
    ) {
        return new ChatRoomResponses(
                memberId,
                rooms,
                page,
                size,
                totalElements,
                totalPages,
                last
        );
    }

    public static ChatRoomResponses empty(Long memberId, Pageable pageable) {
        return new ChatRoomResponses(
                memberId,
                List.of(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                EMPTY_TOTAL_ELEMENTS,
                EMPTY_TOTAL_PAGE,
                true
        );
    }
}
