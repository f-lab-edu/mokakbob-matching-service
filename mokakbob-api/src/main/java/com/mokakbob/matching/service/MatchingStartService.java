package com.mokakbob.matching.service;

import com.mokakbob.common.exception.ApiException;
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
    private static final String LOCK_KEY_PREFIX = "lock:member:";
    private static final String LOCK_KEY_SUFFIX = ":participation";

    private final MatchingTransactionService matchingTransactionService;
    private final RedissonClient redissonClient;

    public void participateMatchingWithLock(double lat, double lng, MatchingCategory category, int participantCount,
                                            Long memberId) {
        String lockKey = LOCK_KEY_PREFIX + memberId + LOCK_KEY_SUFFIX;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LIMIT_LOCK_CATCH_TIME, LOCK_DURATION_TIME, TimeUnit.SECONDS)) {
                matchingTransactionService.participateMatching(lat, lng, category, participantCount, memberId);
            } else {
                throw new ApiException(MatchingErrorCode.EXIST_MATCHING);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(MatchingErrorCode.MATCHING_OPERATION_INTERRUPTED, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
