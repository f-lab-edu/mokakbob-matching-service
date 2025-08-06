package com.mokakbob.matching.domain;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.util.List;

public interface CategoryQueueStore {
    void addToQueue(MatchingCategory category, int count, Long memberId);
    void removeFromQueue(MatchingCategory category, int count, Long memberId);
    List<Long> findWaitingUsers(MatchingCategory category, int count);
}
