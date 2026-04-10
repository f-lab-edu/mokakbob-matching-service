package com.mokakbob.point.facade;

import com.mokakbob.common.exception.ApiException;
import com.mokakbob.domain.point.domain.Payment;
import com.mokakbob.domain.point.domain.vo.PayType;
import com.mokakbob.domain.point.port.dto.VerifiedPayment;
import com.mokakbob.point.exception.PointErrorCode;
import com.mokakbob.point.service.IamportVerificationService;
import com.mokakbob.point.service.PointChargeService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointChargeFacade {

    private static final int LIMIT_LOCK_CATCH_TIME = 5;
    private static final int LOCK_DURATION_TIME = 10;
    private static final String LOCK_KEY_PREFIX = "lock:member:";
    private static final String LOCK_KEY_SUFFIX = ":point";

    private final IamportVerificationService verificationService;
    private final PointChargeService pointChargeService;
    private final RedissonClient redissonClient;

    public Payment charge(Long memberId, int amount, String impUid, String merchantUid, PayType payType) {
        VerifiedPayment verified = verificationService.verify(impUid, merchantUid);

        if (verified.amount() != amount) {
            throw new ApiException(PointErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        String lockKey = LOCK_KEY_PREFIX + memberId + LOCK_KEY_SUFFIX;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(LIMIT_LOCK_CATCH_TIME, LOCK_DURATION_TIME, TimeUnit.SECONDS);

            if (!acquired) {
                throw new ApiException(PointErrorCode.EXIST_POINT_CHARGE);
            }

            return pointChargeService.charge(memberId, amount, impUid, merchantUid, payType);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(PointErrorCode.POINT_OPERATION_INTERRUPTED, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
