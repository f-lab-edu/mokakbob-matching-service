package com.mokakbob.point.controller.request;

import com.mokakbob.domain.point.domain.vo.PayType;

public record PointChargeRequest(
        String impUid,
        String merchantUid,
        int amount,
        PayType payType
) {
}
