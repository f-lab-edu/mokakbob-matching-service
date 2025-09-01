package com.mokakbob.matching.service.request;

import java.time.LocalDateTime;

public record NotificationRequest(
        Long memberId,
        String type,
        String payload,
        LocalDateTime expireAt
) {
}
