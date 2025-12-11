package com.mokakbob.domain.point.service;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.point.domain.Payment;
import com.mokakbob.domain.point.domain.vo.PayType;
import com.mokakbob.domain.point.exception.PointErrorCode;
import com.mokakbob.domain.point.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void validateDuplicateImpUid(String impUid) {
        if (paymentRepository.existsByImpUid(impUid)) {
            throw new DomainException(PointErrorCode.DUPLICATE_PAYMENT);
        }
    }

    @Transactional
    public Payment savePaidStatus(Long memberId, int amount, PayType payType, String impUid, String merchantUid) {
        Payment payment = Payment.paid(memberId, amount, impUid, merchantUid, payType);

        return paymentRepository.save(payment);
    }
}
