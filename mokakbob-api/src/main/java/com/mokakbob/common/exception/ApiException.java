package com.mokakbob.common.exception;

public class ApiException extends BaseException {

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public ApiErrorCode getErrorCode() {
        return (ApiErrorCode) super.getErrorCode();
    }
}
