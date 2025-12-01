package com.mokakbob.chat.service.support;

import java.time.LocalDateTime;

public record CursorToken(
        LocalDateTime createdAt,
        Long id
) {

    public boolean isEmpty() {
        return createdAt == null || id == null;
    }
}
