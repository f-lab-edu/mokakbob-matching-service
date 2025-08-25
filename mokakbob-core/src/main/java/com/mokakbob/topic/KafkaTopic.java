package com.mokakbob.topic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaTopic {

    public static final String MATCHING_PARTICIPATE = "matching.participate";
    public static final String MATCHING_FOUND       = "matching.found";
    public static final String MATCHING_SUCCESS     = "matching.success";
}
