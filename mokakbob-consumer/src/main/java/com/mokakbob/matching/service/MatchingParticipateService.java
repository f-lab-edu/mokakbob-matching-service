package com.mokakbob.matching.service;

import com.mokakbob.cache.CategoryQueueStore;
import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.event.MatchingParticipateEvent;
import com.mokakbob.matching.common.exception.exceptions.ConsumerException;
import com.mokakbob.matching.exception.MatchingConsumerErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private final CategoryQueueStore queueStore;
    private final ParticipantGeoStore geoStore;
    private static final double RADIUS_METERS = 1500.0;

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


        // 오래된 사용자 위치 조회 및 해당 정보로 근처 사용자 가져오기
        double[] location = findMemberDelimiterPlace(category, participantCount, memberDelimiter);
        List<Long> nearby = geoStore.findNearbyMembers(
                category,
                participantCount,
                location[1],
                location[0],
                RADIUS_METERS
        );

        // 기준이 되는 오래된 사용자 자신을 제외한 인원 수 검증
        List<Long> candidates = nearby.stream()
                .filter(id -> !id.equals(memberDelimiter))
                .toList();

        if (candidates.size() < participantCount - 1) {
            return;
        }
    }

    private double[] findMemberDelimiterPlace(MatchingCategory category, int count, Long memberId) {
        return geoStore.getLocation(category, count, memberId)
                .orElseThrow(() -> new ConsumerException(MatchingConsumerErrorCode.NOT_FOUND_LOCATION));
    }
}
