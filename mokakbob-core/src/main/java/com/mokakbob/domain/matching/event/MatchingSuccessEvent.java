package com.mokakbob.domain.matching.event;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.time.Instant;
import java.util.List;

public record MatchingSuccessEvent(
        String key,
        MatchingCategory category,
        int participantCount,
        List<Long> matched,
        Instant createAt
) {
}
