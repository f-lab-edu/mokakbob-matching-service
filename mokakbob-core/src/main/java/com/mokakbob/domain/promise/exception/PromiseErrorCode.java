package com.mokakbob.domain.promise.exception;

import com.mokakbob.domain.exception.DomainErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromiseErrorCode implements DomainErrorCode {
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
