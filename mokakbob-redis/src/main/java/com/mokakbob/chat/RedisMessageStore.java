package com.mokakbob.chat;

import com.mokakbob.cache.ChatMessageStore;
import com.mokakbob.domain.chat.cache.CachedChatMessage;
import com.mokakbob.domain.chat.cursor.CursorToken;
import com.mokakbob.domain.chat.domain.ChatMessage;
import com.mokakbob.config.RedisConstants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RedisMessageStore implements ChatMessageStore {

    private static final int CACHE_LIMIT = 200;
    private static final int REDIS_SCAN_END = CACHE_LIMIT - 1;
    private static final int SERIALIZE_PARTS = 5;

    private final StringRedisTemplate redis;

    @Override
    public void cacheMessage(ChatMessage msg) {
        String key = key(msg.getChatRoomId());
        redis.opsForList().leftPush(key, serialize(msg));
        redis.opsForList().trim(key, 0, CACHE_LIMIT - 1);
    }

    @Override
    public List<CachedChatMessage> loadMessages(Long roomId, String rawCursor, int sizePlusOne) {
        String key = key(roomId);

        return (rawCursor == null || rawCursor.isBlank())
                ? loadLatest(key, sizePlusOne)
                : loadOlderThanCursor(key, parseCursor(rawCursor), sizePlusOne);
    }

    private List<CachedChatMessage> loadLatest(String key, int sizePlusOne) {
        List<String> raw = redis.opsForList().range(key, 0, sizePlusOne - 1);

        return deserializeList(raw);
    }

    private List<CachedChatMessage> loadOlderThanCursor(String key, CursorToken cursor, int sizePlusOne) {
        List<String> all = redis.opsForList().range(key, 0, REDIS_SCAN_END);

        if (all == null || all.isEmpty()) {
            return List.of();
        }

        List<CachedChatMessage> result = new ArrayList<>();

        for (String raw : all) {
            CachedChatMessage msg = deserialize(raw);

            if (isOlder(msg, cursor)) {
                result.add(msg);

                if (result.size() >= sizePlusOne) {
                    break;
                }
            }
        }

        return result;
    }

    private boolean isOlder(CachedChatMessage msg, CursorToken cursor) {
        if (msg.createdAt().isBefore(cursor.createdAt())) {
            return true;
        }

        return msg.createdAt().isEqual(cursor.createdAt()) && msg.id() < cursor.id();
    }

    private List<CachedChatMessage> deserializeList(List<String> rawList) {
        if (rawList == null) {
            return List.of();
        }

        return rawList.stream().map(this::deserialize).toList();
    }

    private String key(Long roomId) {
        return String.format(RedisConstants.CHAT_ROOM_MESSAGES_KEY, roomId);
    }

    private String serialize(ChatMessage m) {
        return m.getCreatedAt() + RedisConstants.CHAT_DELIMITER
                + m.getId() + RedisConstants.CHAT_DELIMITER
                + m.getChatRoomId() + RedisConstants.CHAT_DELIMITER
                + m.getSenderId() + RedisConstants.CHAT_DELIMITER
                + m.getMessage();
    }

    private CachedChatMessage deserialize(String raw) {
        String[] p = raw.split(Pattern.quote(RedisConstants.CHAT_DELIMITER), SERIALIZE_PARTS);

        return new CachedChatMessage(
                Long.parseLong(p[1]),
                Long.parseLong(p[2]),
                Long.parseLong(p[3]),
                p[4],
                LocalDateTime.parse(p[0])
        );
    }

    private CursorToken parseCursor(String cursor) {
        String[] p = cursor.split(Pattern.quote(RedisConstants.CHAT_DELIMITER));

        return new CursorToken(LocalDateTime.parse(p[0]), Long.parseLong(p[1]));
    }
}
