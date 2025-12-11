package com.mokakbob.point.controller;

import com.mokakbob.common.path.point.PointPath;
import com.mokakbob.domain.point.domain.Payment;
import com.mokakbob.global.resolver.annotation.MemberId;
import com.mokakbob.point.controller.request.PointChargeRequest;
import com.mokakbob.point.controller.response.PointChargeResponse;
import com.mokakbob.point.facade.PointChargeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointChargeFacade pointChargeFacade;

    @PostMapping(PointPath.CHARGE)
    public ResponseEntity<PointChargeResponse> chargePoint(
            @MemberId Long memberId,
            @RequestBody PointChargeRequest request
    ) {
        Payment payment = pointChargeFacade.charge(
                memberId,
                request.amount(),
                request.impUid(),
                request.merchantUid(),
                request.payType()
        );

        return ResponseEntity.ok(new PointChargeResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPayType(),
                payment.getStatus()
        ));
    }
}
