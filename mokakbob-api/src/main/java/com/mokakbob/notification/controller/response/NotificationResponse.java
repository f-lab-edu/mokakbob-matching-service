package com.mokakbob.notification.controller.response;

import com.mokakbob.domain.matching.domain.Notification;

public record NotificationResponse(
        Notification notifications
) {
}
