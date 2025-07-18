package com.mokakbob.domain.matching.repository;

import com.mokakbob.domain.matching.domain.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {
}
