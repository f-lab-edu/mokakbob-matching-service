package com.mokakbob.point.controller;

import com.mokakbob.common.path.point.PointPath;
import com.mokakbob.global.resolver.annotation.MemberId;
import com.mokakbob.point.controller.request.PointChargeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    @PostMapping(PointPath.CHARGE)
    public ResponseEntity<Void> chargePoint(
            @MemberId Long memberId,
            @RequestBody PointChargeRequest request
    ) {
        return ResponseEntity.ok()
                .build();
    }
}
