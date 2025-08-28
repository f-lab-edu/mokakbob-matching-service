package com.mokakbob.domain.matching.event;

import com.mokakbob.domain.matching.domain.vo.Location;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;

public record MatchingParticipateEvent(
        String idempotencyKey,
        Long memberId,
        Location location,
        MatchingCategory category,
        int participantCount
) {
}
