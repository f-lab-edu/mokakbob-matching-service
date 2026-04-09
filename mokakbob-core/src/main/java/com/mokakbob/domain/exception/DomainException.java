package com.mokakbob.domain.exception;

import com.mokakbob.common.exception.BaseException;

public class DomainException extends BaseException {

    public DomainException(DomainErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public DomainErrorCode getErrorCode() {
        return (DomainErrorCode) super.getErrorCode();
    }
}
