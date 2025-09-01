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

    private static final long NOTIFICATION_TTL_SECONDS = 30;

    private final NotificationStore notificationStore;
    private final MatchingNotificationService matchingNotificationService;

    public Optional<Notification> getNotification(Long memberId) {
        return notificationStore.findByMemberId(memberId);
    }

    public void respondNotification(Long memberId, boolean isAccept) {
        Notification notification = notificationStore.findById(memberId);

        if(notification.isResponded()) {
            throw new ApiException(NotificationErrorCode.ALREADY_RESPONDED);
        }

        notification.updateResponse(isAccept);
        notificationStore.save(notification, NOTIFICATION_TTL_SECONDS); // 알림 갱신 및 후처리를 위한 시간 설정

        matchingNotificationService.handleResponse(notification);
    }
}
