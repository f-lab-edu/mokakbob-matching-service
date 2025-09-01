package com.mokakbob.matching;

import com.mokakbob.cache.NotificationStore;
import com.mokakbob.domain.matching.domain.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRedisStore implements NotificationStore {

    @Override
    public void save(Notification notification, long ttlSeconds) {
        // todo
    }

    @Override
    public Notification findById(Long memberId, Long notificationId) {
        // todo
        return null;
    }

    @Override
    public List<Notification> findAllByMemberId(Long memberId) {
        // todo
        return List.of();
    }

    @Override
    public void delete(Long memberId, Long notificationId) {
        // todo
    }

    @Override
    public void updateResponse(Long memberId, Long notificationId, boolean accepted) {
        // todo
    }
}
