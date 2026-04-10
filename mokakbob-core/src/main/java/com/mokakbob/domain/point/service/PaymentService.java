package com.mokakbob.domain.point.service;

import com.mokakbob.domain.exception.DomainException;
import com.mokakbob.domain.point.domain.Payment;
import com.mokakbob.domain.point.domain.vo.PayType;
import com.mokakbob.domain.point.exception.PointErrorCode;
import com.mokakbob.domain.point.repository.PaymentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment savePaidStatus(Long memberId, int amount, PayType payType, String impUid, String merchantUid) {
        Payment payment = Payment.paid(memberId, amount, impUid, merchantUid, payType);

        try {
            return paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new DomainException(PointErrorCode.DUPLICATE_PAYMENT, e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findPaymentByImpUid(String impUid) {
        return paymentRepository.findByImpUid(impUid);
    }
}
