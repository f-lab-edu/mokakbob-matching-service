package com.mokakbob.domain.matching.repository;

import com.mokakbob.domain.matching.domain.MatchingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Long> {

    boolean existsByMatchingIdAndMemberIdAndIsAcceptedTrue(Long matchingId, Long memberId);
}
