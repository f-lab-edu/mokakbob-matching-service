package com.mokakbob.domain.matching.service;

import com.mokakbob.domain.matching.domain.MatchingRequest;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.domain.vo.MatchingRequestStatus;
import com.mokakbob.domain.matching.repository.MatchingRequestRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRequestRepository requestRepository;

    @Transactional
    public void saveMatchingRequest(Long memberId, MatchingCategory category, int groupSize, BigDecimal lat, BigDecimal lng) {
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .memberId(memberId)
                .matchingCategory(category)
                .groupSize(groupSize)
                .latitude(lat)
                .longitude(lng)
                .status(MatchingRequestStatus.PARTICIPATE)
                .build();

        requestRepository.save(matchingRequest);
    }
}
