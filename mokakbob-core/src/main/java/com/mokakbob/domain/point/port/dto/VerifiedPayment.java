package com.mokakbob.domain.point.port.dto;

public record VerifiedPayment(
        String impUid,
        String merchantUid,
        int amount,
        String status,
        String pgProvider
) {
}
