package com.mokakbob.topic;

import lombok.Getter;

@Getter
public enum KafkaTopic {

    // 매칭 참여 토픽
    MATCHING_PARTICIPATE("matching.participate"),
    MATCHING_FOUND("matching.found"),
    MATCHING_SUCCESS("matching.success")
    ;

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }
}
