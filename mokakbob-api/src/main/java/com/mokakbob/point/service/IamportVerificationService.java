package com.mokakbob.point.service;

import com.mokakbob.common.exception.ApiException;
import com.mokakbob.domain.point.port.PaymentVerificationPort;
import com.mokakbob.domain.point.port.dto.VerifiedPayment;
import com.mokakbob.point.exception.PointErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IamportVerificationService {

    private static final String PAID_STATUS = "paid";

    private final PaymentVerificationPort verificationPort;

    public VerifiedPayment verify(String impUid, String merchantUid) {

        VerifiedPayment verified = verificationPort.verify(impUid, merchantUid);

        // 결제 상태 확인
        if (!PAID_STATUS.equalsIgnoreCase(verified.status())) {
            throw new ApiException(PointErrorCode.INVALID_PAYMENT);
        }

        // merchantUid 일치 여부 확인
        if (!merchantUid.equals(verified.merchantUid())) {
            throw new ApiException(PointErrorCode.INVALID_PAYMENT);
        }

        return verified;
    }
}
