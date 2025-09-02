package com.mokakbob.notification.controller;

import com.mokakbob.common.path.notification.NotificationPath;
import com.mokakbob.global.resolver.annotation.MemberId;
import com.mokakbob.notification.controller.response.NotificationResponse;
import com.mokakbob.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(NotificationPath.POLL)
    public ResponseEntity<NotificationResponse> getMatchingNotification(
            @MemberId Long memberId
    ) {
        return notificationService.getNotifications(memberId)
                .map(NotificationResponse::of)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(NotificationResponse.empty()));
    }
}
