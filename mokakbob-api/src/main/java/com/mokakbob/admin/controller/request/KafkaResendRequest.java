package com.mokakbob.admin.controller.request;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KafkaResendRequest(
        @NotBlank
        String key,

        @NotBlank
        Long memberId,

        @NotBlank
        double lat,

        @NotBlank
        double lng,

        @NotNull
        MatchingCategory category,

        @NotBlank
        int participantCount
) {
}
