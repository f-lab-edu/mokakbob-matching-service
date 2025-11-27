package com.mokakbob.domain.matching.repository;

import com.mokakbob.domain.matching.domain.MatchingParticipant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Long> {

    boolean existsByMatchingIdAndMemberIdAndIsAcceptedTrue(Long matchingId, Long memberId);
    List<MatchingParticipant> findByMemberId(Long memberId);
    List<MatchingParticipant> findByMatchingId(Long matchingId);
}
