package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.vo.MatchingRequestStatus;

public interface ParticipantStore {
    boolean isAlreadyParticipating(Long memberId);
    void transitionToParticipating(Long memberId);
    void transitionToFound(Long memberId);
    void clearAll(Long memberId);
    MatchingRequestStatus getStatus(Long memberId);
}
