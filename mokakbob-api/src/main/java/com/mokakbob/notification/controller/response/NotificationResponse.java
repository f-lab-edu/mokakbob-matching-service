package com.mokakbob.notification.controller.response;

import com.mokakbob.domain.matching.domain.Notification;
import java.util.List;

public record NotificationResponse(
        List<Notification> notifications
) {
}
