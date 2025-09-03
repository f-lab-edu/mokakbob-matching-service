package com.mokakbob.domain.matching.event;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.util.List;

public record MatchingFoundEvent(
        String key,
        MatchingCategory category,
        int participantCount,
        List<Long> matched
) {
}
