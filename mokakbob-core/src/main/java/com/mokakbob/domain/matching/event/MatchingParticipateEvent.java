package com.mokakbob.domain.matching.event;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;

public record MatchingParticipateEvent(
        Long memberId,
        double lat,
        double lng,
        MatchingCategory category,
        int participantCount
) {
}
