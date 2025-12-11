package com.mokakbob.domain.point.repository;

import com.mokakbob.domain.point.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByImpUid(String impUid);
}
