package com.mokakbob.common.exception;

public class ApiException extends BaseException {

    public ApiException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
