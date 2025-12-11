package com.mokakbob.point.controller.response;

import com.mokakbob.domain.point.domain.vo.PayStatus;
import com.mokakbob.domain.point.domain.vo.PayType;

public record PointChargeResponse(
        Long paymentId,
        int chargedAmount,
        PayType payType,
        PayStatus status
) {
}
