package com.mokakbob.matching.service;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private final CategoryQueueStore queueStore;

    public void participateMatching(MatchingParticipateEvent event) {
        MatchingCategory category = event.category();
        int participantCount = event.participantCount();

        if(!queueStore.hasEnoughForMatching(category, participantCount)) {
            return;
        }

        // [카테고리 / 인원] 기준 가장 오래된 사용자 뽑기
        Optional<Long> oldestMember = queueStore.popOldestMember(category, participantCount);
        if(oldestMember.isEmpty()) {
            return;
        }
        Long memberDelimiter = oldestMember.get();
    }
}
