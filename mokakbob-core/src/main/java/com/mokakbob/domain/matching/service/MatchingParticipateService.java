package com.mokakbob.domain.matching.service;

import com.mokakbob.domain.exception.DomainException;
import com.mokakbob.domain.matching.domain.MatchingParticipant;
import com.mokakbob.domain.matching.exception.MatchingErrorCode;
import com.mokakbob.domain.matching.repository.MatchingParticipantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public Page<MatchingParticipant> findMatchingParticipantsByMemberId(Long memberId, Pageable pageable) {
        return participantRepository.findByMemberId(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public List<MatchingParticipant> findMatchingParticipantByMatchingIds(List<Long> matchingId) {
        return participantRepository.findByMatchingIdIn(matchingId);
    }
}
