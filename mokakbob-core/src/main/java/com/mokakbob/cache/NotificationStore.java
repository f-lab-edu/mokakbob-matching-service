package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationStore {

    void save(String key, List<Notification> notifications, long ttlSeconds);
    Optional<Notification> findByMemberId(Long memberId);
}
