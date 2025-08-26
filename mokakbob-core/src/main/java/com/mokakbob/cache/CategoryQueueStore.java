package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.time.Duration;
import java.util.List;

public interface CategoryQueueStore {
    void addToQueue(MatchingCategory category, int count, Long memberId);
    void removeFromQueue(MatchingCategory category, int count, Long memberId);
    boolean hasEnoughForMatching(MatchingCategory category, int participantCount);
    List<Long> reserveOldestMember(MatchingCategory category, int count, String reserveId, Duration ttl);
    boolean reserveSpecificMember(MatchingCategory category, int count, Long memberId, String reserveId, Duration ttl);
    void commitReservation(String reserveId, MatchingCategory category, int count);
    void rollbackReservation(String reserveId, MatchingCategory category, int count);
}
