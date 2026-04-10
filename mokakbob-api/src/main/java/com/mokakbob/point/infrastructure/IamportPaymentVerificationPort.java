package com.mokakbob.point.infrastructure;

import com.mokakbob.common.exception.ApiException;
import com.mokakbob.domain.point.port.PaymentVerificationPort;
import com.mokakbob.domain.point.port.dto.VerifiedPayment;
import com.mokakbob.point.exception.PointErrorCode;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IamportPaymentVerificationPort implements PaymentVerificationPort {

    private final IamportClient iamportClient;

    @Override
    public VerifiedPayment verify(String impUid, String merchantUid) {
        try {
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
            Payment payment = response.getResponse();

            if (payment == null) {
                log.warn("[Iamport] 결제 정보 없음 impUid={}, merchantUid={}", impUid, merchantUid);
                throw new ApiException(PointErrorCode.INVALID_PAYMENT);
            }

            return new VerifiedPayment(
                    payment.getImpUid(),
                    payment.getMerchantUid(),
                    payment.getAmount().intValueExact(),
                    payment.getStatus(),
                    payment.getPgProvider()
            );

        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            log.error("[Iamport] 결제 검증 중 오류 impUid={}, merchantUid={}", impUid, merchantUid, e);
            throw new ApiException(PointErrorCode.PAYMENT_GATEWAY_ERROR, e);
        }
    }
}
