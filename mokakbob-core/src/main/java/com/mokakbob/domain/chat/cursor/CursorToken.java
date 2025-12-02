package com.mokakbob.domain.chat.cursor;

import java.time.LocalDateTime;

public record CursorToken(
        LocalDateTime createdAt,
        Long id
) {

    public boolean isEmpty() {
        return createdAt == null || id == null;
    }
}
