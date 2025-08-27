package com.mokakbob.pubsub;

import lombok.Getter;

@Getter
public enum RedisChannelType {

    NOTIFICATION_USER("channel:notification:user:%s", "/topic/notification/user/%s")
    ;

    private final String channelPattern;
    private final String destinationPattern;

    RedisChannelType(String channelPattern, String destinationPattern) {
        this.channelPattern = channelPattern;
        this.destinationPattern = destinationPattern;
    }

    public String formatChannel(String memberId) {
        return String.format(channelPattern, memberId);
    }

    public String formatDestination(String memberId) {
        return String.format(destinationPattern, memberId);
    }
}
