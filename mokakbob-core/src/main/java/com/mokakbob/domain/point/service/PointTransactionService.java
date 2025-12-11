package com.mokakbob.domain.point.service;

import com.mokakbob.domain.point.domain.PointTransaction;
import com.mokakbob.domain.point.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointTransactionService {

    private final PointTransactionRepository transactionRepository;

    @Transactional
    public void rechargePoint(Long memberId, int amount) {
        PointTransaction pt = PointTransaction.recharge(memberId, amount);
        transactionRepository.save(pt);
    }
}
