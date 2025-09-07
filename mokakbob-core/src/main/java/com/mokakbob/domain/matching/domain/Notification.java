package com.mokakbob.domain.matching.domain;

import com.mokakbob.common.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Notification extends BaseEntity {

    private final Long memberId;
    private final String key;
    private final String type;
    private final String payload;
}
