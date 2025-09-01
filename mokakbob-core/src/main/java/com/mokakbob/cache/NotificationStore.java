package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.Notification;

public interface NotificationStore {

    void save(Notification notification, long ttlSeconds);
    Notification findById(Long memberId, Long notificationId);
    void delete(Long memberId, Long notificationId);
}
