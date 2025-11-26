package com.mokakbob.domain.matching.service;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.matching.exception.MatchingErrorCode;
import com.mokakbob.domain.matching.repository.MatchingParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingParticipateService {

    private final MatchingParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public void validateMatchingParticipant(Long matchingId, Long memberId) {
        boolean exists = participantRepository
                .existsByMatchingIdAndMemberIdAndIsAcceptedTrue(matchingId, memberId);

        if (!exists) {
            throw new DomainException(MatchingErrorCode.NOT_MATCHING_PARTICIPANT);
        }
    }
}
