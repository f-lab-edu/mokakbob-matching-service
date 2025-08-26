package com.mokakbob.matching.service.request;

public record NotificationRequest(
        Long memberId,
        String type,
        String payload
) {
}
