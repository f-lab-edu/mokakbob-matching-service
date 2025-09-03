package com.mokakbob.notification.controller.response;

import com.mokakbob.domain.matching.domain.Notification;

public record NotificationResponse(
        boolean hasNotification,
        Notification notification
) {

    public static NotificationResponse of(Notification notification) {
        return new NotificationResponse(true, notification);
    }

    public static NotificationResponse empty() {
        return new NotificationResponse(false, null);
    }
}
