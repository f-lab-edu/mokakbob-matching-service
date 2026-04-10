package com.mokakbob.matching.exception;

import com.mokakbob.matching.common.exception.ConsumerErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingConsumerErrorCode implements ConsumerErrorCode {
    ALREADY_MATCHED(false, "C001", "이미 매칭된 사용자입니다."),
    NOT_FOUND_MEMBER(false, "C002", "사용자를 찾을 수 없습니다."),
    MATCHING_LOCK_ACQUIRE_FAILED(true, "C003", "이미 다른 매칭이 진행중입니다. 잠시 후 재시도합니다."),
    MATCHING_LOCK_INTERRUPTED(false, "C005", "매칭 락 대기 중 인터럽트가 발생했습니다."),
    MATCHING_NOTIFICATION_SERIALIZE_FAILED(false, "C006", "매칭 이벤트 직렬화 실패."),
    NOT_FOUND_LOCATION(false, "C007", "위치 정보를 찾을 수 없습니다."),
    MATCHING_PARTICIPATE_CONSUMER_EXCEPTION(false, "C008", "매칭 참여 처리 중 예외가 발생했습니다.")
    ;

    private final boolean isRetryable;
    private final String customCode;
    private final String message;
}
