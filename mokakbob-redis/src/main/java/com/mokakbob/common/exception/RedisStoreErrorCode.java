package com.mokakbob.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisStoreErrorCode implements RedisErrorCode {
    STORE_CONNECTION_FAILED("RS001", "레디스 저장소 연결에 실패했습니다."),
    STORE_OPERATION_FAILED("RS002", "레디스 데이터 처리에 실패했습니다."),
    STORE_SERIALIZATION_FAILED("RS003", "레디스 데이터 직렬화에 실패했습니다."),
    REDIS_STORE_ERROR("RS004", "레디스 저장소 처리 중 오류가 발생했습니다."),
    ;

    private final String customCode;
    private final String message;
}
