package com.mokakbob.common.exception;

public class ApiException extends BaseException {

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode);
    }

    public ApiException(ApiErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    @Override
    public ApiErrorCode getErrorCode() {
        return (ApiErrorCode) super.getErrorCode();
    }
}
