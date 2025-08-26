package com.mokakbob.matching;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.cache.CategoryQueueStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueueRedisStore implements CategoryQueueStore {

    private static final String ZSET_CATEGORY_KEY = "matching:zset:%s:%d";
    private static final String MEMBER_KEY = "member:";

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void addToQueue(MatchingCategory category, int count, Long memberId) {
        String zsetKey = zsetKey(category, count);
        double score = System.currentTimeMillis();

        basicRedisTemplate.opsForZSet()
                .add(zsetKey, memberKey(memberId), score);
    }

    @Override
    public void removeFromQueue(MatchingCategory category, int count, Long memberId) {
        String zsetKey = zsetKey(category, count);

        basicRedisTemplate.opsForZSet()
                .remove(zsetKey, memberKey(memberId));
    }

    @Override
    public boolean hasEnoughForMatching(MatchingCategory category, int participantCount) {
        Long existMembers = basicRedisTemplate.opsForZSet()
                .zCard(zsetKey(category, participantCount));

        return existMembers != null && existMembers >= participantCount;
    }

    private String zsetKey(MatchingCategory category, int participantCount) {
        return ZSET_CATEGORY_KEY.formatted(category.name(), participantCount);
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY + memberId;
    }
}
