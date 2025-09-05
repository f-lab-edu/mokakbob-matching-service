package com.mokakbob.domain.matching.service;

import com.mokakbob.domain.matching.domain.Matching;
import com.mokakbob.domain.matching.domain.MatchingParticipant;
import com.mokakbob.domain.matching.domain.MatchingRequest;
import com.mokakbob.domain.matching.domain.vo.Location;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.domain.matching.domain.vo.MatchingRequestStatus;
import com.mokakbob.domain.matching.domain.vo.MatchingStatus;
import com.mokakbob.domain.matching.repository.MatchingParticipantRepository;
import com.mokakbob.domain.matching.repository.MatchingRepository;
import com.mokakbob.domain.matching.repository.MatchingRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRequestRepository requestRepository;
    private final MatchingRepository matchingRepository;
    private final MatchingParticipantRepository participantRepository;

    @Transactional
    public void saveMatchingRequest(Long memberId, MatchingCategory category, int groupSize, Location location) {
        MatchingRequest matchingRequest = MatchingRequest.builder()
                .memberId(memberId)
                .matchingCategory(category)
                .groupSize(groupSize)
                .location(location)
                .status(MatchingRequestStatus.PARTICIPATE)
                .build();

        requestRepository.save(matchingRequest);
    }

    @Transactional
    public Matching saveMatching(MatchingCategory category) {
        return matchingRepository.save(
                Matching.builder()
                        .category(category)
                        .status(MatchingStatus.MATCHED)
                        .build()
        );
    }

    @Transactional
    public void saveMatchingParticipants(Matching matching, List<Long> memberIds) {
        List<MatchingParticipant> participants = memberIds.stream()
                .map(memberId -> MatchingParticipant.builder()
                        .matchingId(matching.getId())
                        .memberId(memberId)
                        .isAccepted(true)
                        .build())
                .toList();

        participantRepository.saveAll(participants);
    }
}
