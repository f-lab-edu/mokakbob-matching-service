package com.mokakbob.matching.infrastructure;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.matching.domain.CategoryQueueStore;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueueRedisStore implements CategoryQueueStore {

    private static final String ZSET_CATEGORY_KEY = "matching:zset:%s:%d";
    private static final String MEMBER_KEY = "member:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addToQueue(MatchingCategory category, int count, Long memberId) {
        String zsetKey = zsetKey(category, count);
        double score = System.currentTimeMillis();

        redisTemplate.opsForZSet()
                .add(zsetKey, memberKey(memberId), score);
    }

    @Override
    public void removeFromQueue(MatchingCategory category, int count, Long memberId) {
        String zsetKey = zsetKey(category, count);

        redisTemplate.opsForZSet()
                .remove(zsetKey, memberKey(memberId));
    }

    @Override
    public List<Long> findWaitingUsers(MatchingCategory category, int count) {
        String zsetKey = zsetKey(category, count);

        Set<String> members = redisTemplate.opsForZSet()
                .range(zsetKey, 0, count - 1);

        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        return members.stream()
                .map(this::extractMemberId)
                .flatMap(Optional::stream)
                .toList();
    }

    private String zsetKey(MatchingCategory category, int participantCount) {
        return ZSET_CATEGORY_KEY.formatted(category.name(), participantCount);
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY + memberId;
    }

    private Optional<Long> extractMemberId(String value) {
        if (value != null && value.startsWith(MEMBER_KEY)) {
            try {
                return Optional.of(Long.parseLong(value.substring(7)));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
