package com.mokakbob.point.service;

import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.domain.point.domain.Payment;
import com.mokakbob.domain.point.domain.vo.PayType;
import com.mokakbob.domain.point.service.PaymentService;
import com.mokakbob.domain.point.service.PointTransactionService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointChargeService {

    private final MemberService memberService;
    private final PaymentService paymentService;
    private final PointTransactionService pointTransactionService;

    /**
     * PG 검증 이후, 금액 검증까지 완료된 상태에서 도메인 충전 처리 수행
     */
    @Transactional
    public Payment charge(Long memberId, int amount, String impUid, String merchantUid, PayType payType) {
        Optional<Payment> existing = paymentService.findPaymentByImpUid(impUid);

        if (existing.isPresent()) {
            return existing.get();
        }

        Payment payment = paymentService.savePaidStatus(memberId, amount, payType, impUid, merchantUid);
        memberService.addPoint(memberId, amount);
        pointTransactionService.rechargePoint(memberId, amount);

        return payment;
    }
}
