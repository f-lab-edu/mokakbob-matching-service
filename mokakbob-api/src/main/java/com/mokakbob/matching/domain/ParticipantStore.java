package com.mokakbob.matching.domain;

import com.mokakbob.domain.matching.domain.vo.MatchingRequestStatus;

public interface ParticipantStore {
    boolean isAlreadyParticipating(Long memberId);
    void transitionToParticipating(Long memberId);
    void transitionToFound(Long memberId);
    void clearAll(Long memberId);
    MatchingRequestStatus getStatue(Long memberId);
}
