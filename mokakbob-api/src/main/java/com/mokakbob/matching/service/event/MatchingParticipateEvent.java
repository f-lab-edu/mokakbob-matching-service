package com.mokakbob.matching.service.event;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;

public record MatchingParticipateEvent(
        Long memberId,
        double lat,
        double lng,
        MatchingCategory category,
        int participantCount
) {
}
