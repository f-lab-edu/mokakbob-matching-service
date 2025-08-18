package com.mokakbob.matching.service;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.matching.exception.MatchingErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingStartService {

    private static final int LIMIT_LOCK_CATCH_TIME = 3;
    private static final int LOCK_DURATION_TIME = 10;

    private final MatchingTransactionService matchingTransactionService;
    private final RedissonClient redissonClient;

    public void participateMatchingWithLock(double lat, double lng, MatchingCategory category, int participantCount,
                                            Long memberId) {
        String lockKey = "lock:member:" + memberId + ":participation";
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LIMIT_LOCK_CATCH_TIME, LOCK_DURATION_TIME, TimeUnit.SECONDS)) {
                matchingTransactionService.participateMatching(lat, lng, category, participantCount, memberId);
            } else {
                throw new ApiException(MatchingErrorCode.EXIST_MATCHING);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(MatchingErrorCode.MATCHING_OPERATION_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
