package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.Notification;
import java.util.List;

public interface NotificationStore {

    void save(Notification notification, long ttlSeconds);
    Notification findById(Long memberId, Long notificationId);
    List<Notification> findAllByMemberId(Long memberId);
    void delete(Long memberId, Long notificationId);
    void updateResponse(Long memberId, Long notificationId, boolean accepted);
}
