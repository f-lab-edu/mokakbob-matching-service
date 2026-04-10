package com.mokakbob.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisPubSubErrorCode implements RedisErrorCode {
    PUBSUB_CONNECTION_FAILED("RPS001", "레디스 Pub/Sub 연결에 실패했습니다."),
    PUBSUB_SUBSCRIBE_FAILED("RPS002", "레디스 채널 구독에 실패했습니다."),
    PUBSUB_PUBLISH_FAILED("RPS003", "메시지 발행에 실패했습니다."),
    REDIS_PUBLISH_ERROR("RPS004", "레디스 메시지 발행 중 오류가 발생했습니다."),
    REDIS_SUBSCRIBE_ERROR("RPS005", "레디스 메시지 구독 중 오류가 발생했습니다."),
    ;

    private final String customCode;
    private final String message;
}
