package com.mokakbob.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Redis 관련 매직 스트링 및 규칙을 중앙 관리하는 상수 클래스입니다.
 * (DRY 원칙 및 무결성 확보용)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstants {

    // CHAT
    public static final String CHAT_CHANNEL_PREFIX = "chat.";
    public static final String CHAT_ROOM_MESSAGES_KEY = "chat:room:%d:messages";
    public static final String CHAT_DELIMITER = "|";
    
    // NOTIFICATION
    public static final String NOTIFICATION_ROOM_PREFIX = "notification:room:";
    public static final String NOTIFICATION_MEMBER_PREFIX = "notification:member:";
    
    // AUTH
    public static final String BLACKLIST_KEY_PREFIX = "logout:";
    public static final String BLACKLIST_VALUE = "logout";
    
    // PARTICIPANT
    public static final String PARTICIPANT_KEY_PREFIX = "participant:";
    public static final String PARTICIPANT_GEO_KEY = "participant:geo";

    // CORE
    public static final String REDIS_PROTOCOL_PREFIX = "redis://";

    // COMMON ERROR MESSAGES (MagicString Cleanup)
    public static final String ERR_KEY_NULL = "key must not be null";
    public static final String ERR_MEMBER_ID_NULL = "memberId must not be null";
    public static final String ERR_NOTIFICATION_NULL = "notification must not be null";
}
