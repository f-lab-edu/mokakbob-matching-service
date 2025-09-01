package com.mokakbob.domain.matching.domain;

import com.mokakbob.common.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Notification extends BaseEntity {

    private final Long id;
    private final Long memberId;
    private final String type;
    private final String payload;

    @Builder.Default
    private final boolean responded = false;

    @Builder.Default
    private final Boolean accepted = null;
}
