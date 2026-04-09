package com.mokakbob.common.exception;

public enum RedisStoreErrorCode implements RedisErrorCode {

    REDIS_STORE_ERROR(500, "R001", "레디스 저장 과정에서 오류가 발생했습니다."),
    REDIS_CONVERT_ERROR(500, "R002", "데이터 변환 중 오류가 발생했습니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;

    RedisStoreErrorCode(int httpStatus, String customCode, String message) {
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
