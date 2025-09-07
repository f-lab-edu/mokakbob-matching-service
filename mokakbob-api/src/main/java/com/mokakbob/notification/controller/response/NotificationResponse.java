package com.mokakbob.notification.controller.response;

import com.mokakbob.domain.matching.domain.Notification;

public record NotificationResponse(
        Notification notification
) {

    public static NotificationResponse of(Notification notification) {
        return new NotificationResponse(notification);
    }

    public static NotificationResponse empty() {
        return new NotificationResponse(null);
    }
}
