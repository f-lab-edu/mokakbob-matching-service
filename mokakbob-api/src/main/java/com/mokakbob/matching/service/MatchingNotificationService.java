package com.mokakbob.matching.service;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.Notification;
import com.mokakbob.matching.exception.MatchingErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingNotificationService {

    private static final int LIMIT_LOCK_CATCH_TIME_SECONDS = 3;
    private static final int LOCK_DURATION_TIME_SECONDS = 5;

    private final RedissonClient redissonClient;

    public void lockNotificationRequest(Notification notification) {
        String idempotencyKey = notification.getKey();
        RLock lock = redissonClient.getLock(idempotencyKey + ":lock");

        try {
            if (!lock.tryLock(LIMIT_LOCK_CATCH_TIME_SECONDS, LOCK_DURATION_TIME_SECONDS, TimeUnit.SECONDS)) {
                throw new ApiException(MatchingErrorCode.ALREADY_PROCESSING_NOTIFICATION);
            }

            handleNotification(notification);

        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
            throw new ApiException(MatchingErrorCode.ALREADY_PROCESSING_NOTIFICATION);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void handleNotification(Notification notification) {
        // todo
    }
}
