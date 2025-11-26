package com.mokakbob.domain.matching.exception;

import com.mokakbob.common.exception.DomainErrorCode;

public enum MatchingErrorCode implements DomainErrorCode {

    // matchingParticipant
    NOT_MATCHING_PARTICIPANT(401, "P001", "해당 매칭에 속하는 참여자가 아닙니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    MatchingErrorCode(int httpStatus, String customCode, String message) {
        this.httpStatus = httpStatus;
        this.customCode = customCode;
        this.message = message;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String customCode() {
        return customCode;
    }

    @Override
    public String message() {
        return message;
    }
}
