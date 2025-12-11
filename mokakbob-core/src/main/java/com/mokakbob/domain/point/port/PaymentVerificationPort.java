package com.mokakbob.domain.point.port;

import com.mokakbob.domain.point.port.dto.VerifiedPayment;

public interface PaymentVerificationPort {

    VerifiedPayment verify(String impUid, String merchantUid);
}
