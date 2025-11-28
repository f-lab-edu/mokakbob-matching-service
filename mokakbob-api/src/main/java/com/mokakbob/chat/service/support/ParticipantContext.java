package com.mokakbob.chat.service.support;

import com.mokakbob.domain.matching.domain.MatchingParticipant;
import com.mokakbob.domain.member.domain.Member;
import java.util.List;
import java.util.Map;

/**
 * 특정 매칭 ID들을 기준으로 조회된
 * - 매칭 참여자 목록
 * - 회원 정보
 * 두 가지 데이터를 서비스 계층에서 효율적으로 접근하기 위한 컨텍스트 객체.
 */
public record ParticipantContext(
        Map<Long, List<MatchingParticipant>> participantsByMatchingId,
        Map<Long, Member> memberMap
) {
}
