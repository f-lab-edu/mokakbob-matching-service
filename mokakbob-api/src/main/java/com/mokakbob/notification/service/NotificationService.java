package com.mokakbob.notification.service;

import com.mokakbob.cache.NotificationStore;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.Notification;
import java.util.Optional;
import com.mokakbob.matching.service.MatchingNotificationService;
import com.mokakbob.notification.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationStore notificationStore;
    private final MatchingNotificationService matchingNotificationService;

    public Optional<Notification> getNotification(Long memberId) {
        return notificationStore.findByMemberId(memberId);
    }

    public void respondNotification(Long memberId, boolean isAccept) {
        Notification notification = notificationStore.findByMemberId(memberId)
                .orElseThrow(() -> new ApiException(NotificationErrorCode.NOT_FOUND_NOTIFICATION));

        if (notification.isResponded()) {
            throw new ApiException(NotificationErrorCode.ALREADY_RESPONDED);
        }

        notification.updateResponse(isAccept);

        matchingNotificationService.lockNotificationRequest(notification);
    }
}
