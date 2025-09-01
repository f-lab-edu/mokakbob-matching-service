package com.mokakbob.notification.service;

import com.mokakbob.cache.NotificationStore;
import com.mokakbob.domain.matching.domain.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationStore notificationStore;

    public List<Notification> getNotifications(Long memberId) {
        return notificationStore.findAllByMemberId(memberId);
    }
}
