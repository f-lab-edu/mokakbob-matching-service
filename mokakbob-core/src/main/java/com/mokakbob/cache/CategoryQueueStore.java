package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.util.Optional;

public interface CategoryQueueStore {
    void addToQueue(MatchingCategory category, int count, Long memberId);
    void removeFromQueue(MatchingCategory category, int count, Long memberId);
    boolean hasEnoughForMatching(MatchingCategory category, int participantCount);
    Optional<Long> popOldestMember(MatchingCategory category, int count);
}
