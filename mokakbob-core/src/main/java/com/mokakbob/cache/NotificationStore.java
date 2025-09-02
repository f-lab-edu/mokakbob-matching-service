package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.Notification;
import java.util.List;

public interface NotificationStore {

    void save(String key, List<Notification> notifications, long ttlSeconds);
    Notification findByMemberId(Long memberId);
}
