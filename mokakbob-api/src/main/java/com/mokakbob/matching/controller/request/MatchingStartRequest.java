package com.mokakbob.matching.controller.request;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MatchingStartRequest(
        @NotBlank
        @DecimalMin("-90.0") @DecimalMax("90.0")
        Double lat,

        @NotBlank
        @DecimalMin("-180.0") @DecimalMax("180.0")
        Double lng,

        @NotBlank
        MatchingCategory category,

        @Min(1) @Max(4)
        int participantCount
) {
}
