package com.mokakbob.point.facade;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.domain.point.domain.Payment;
import com.mokakbob.domain.point.domain.vo.PayType;
import com.mokakbob.domain.point.port.dto.VerifiedPayment;
import com.mokakbob.point.exception.PointErrorCode;
import com.mokakbob.point.service.IamportVerificationService;
import com.mokakbob.point.service.PointChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointChargeFacade {

    private final IamportVerificationService verificationService;
    private final PointChargeService pointChargeService;

    public Payment charge(Long memberId, int amount, String impUid, String merchantUid, PayType payType) {
        VerifiedPayment verified = verificationService.verify(impUid, merchantUid);

        if (verified.amount() != amount) {
            throw new ApiException(PointErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        return pointChargeService.charge(memberId, amount, impUid, merchantUid, payType);
    }
}
