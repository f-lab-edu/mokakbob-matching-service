package com.mokakbob.domain.matching.repository;

import com.mokakbob.domain.matching.domain.MatchingParticipant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Long> {

    boolean existsByMatchingIdAndMemberIdAndIsAcceptedTrue(Long matchingId, Long memberId);

    Page<MatchingParticipant> findByMemberId(Long memberId, Pageable pageable);

    List<MatchingParticipant> findByMatchingIdIn(List<Long> matchingIds);
}
