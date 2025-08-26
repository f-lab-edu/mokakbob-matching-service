package com.mokakbob.matching;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.cache.CategoryQueueStore;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueueRedisStore implements CategoryQueueStore {

    private static final String ZSET_CATEGORY_KEY = "matching:zset:%s:%d";
    private static final String MEMBER_KEY = "member:";
    private static final String RESERVE_KEY = "matching:reserve:%s";

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

    @Override
    public List<Long> reserveOldestMember(MatchingCategory category, int count, String reserveId, Duration ttl) {
        String key = zsetKey(category, count);

        // 가장 오래된 사용자 하나 뽑기
        Set<ZSetOperations.TypedTuple<String>> popped = basicRedisTemplate.opsForZSet().popMin(key, 1);
        if (popped == null || popped.isEmpty()) {
            return List.of();
        }

        ZSetOperations.TypedTuple<String> tuple = popped.iterator().next();
        String value = tuple.getValue();
        Double originalScore = tuple.getScore(); // 원래 score
        Long memberId = extractMemberId(value).orElse(null);

        if (memberId == null || originalScore == null) {
            return List.of();
        }

        // 예약 키
        String reserveKey = RESERVE_KEY.formatted(reserveId);
        basicRedisTemplate.opsForList()
                .rightPush(reserveKey, memberId + ":" + originalScore);
        basicRedisTemplate.expire(reserveKey, ttl);

        return List.of(memberId);
    }

    @Override
    public boolean reserveSpecificMember(MatchingCategory category, int count, Long memberId, String reserveId,
                                         Duration ttl) {
        String zsetKey = zsetKey(category, count);
        String memberKey = memberKey(memberId);

        // 원래 score 조회
        Double originalScore = basicRedisTemplate.opsForZSet()
                .score(zsetKey, memberKey);
        if (originalScore == null) { // 큐에 없거나 이미 다른 데서 pop 된 경우
            return false;
        }

        // 예약 상태로 빼기
        Long removed = basicRedisTemplate.opsForZSet()
                .remove(zsetKey, memberKey);
        if (removed == null || removed == 0) { // 이미 누군가 빼간 경우
            return false;
        }

        // 예약 리스트에 저장
        String reserveKey = RESERVE_KEY.formatted(reserveId);
        basicRedisTemplate.opsForList()
                .rightPush(reserveKey, memberId + ":" + originalScore);

        // TTL 연장
        basicRedisTemplate.expire(reserveKey, ttl);

        return true;
    }

    @Override
    public void commitReservation(String reserveId, MatchingCategory category, int count) {
        basicRedisTemplate.delete(RESERVE_KEY.formatted(reserveId));
    }

    @Override
    public void rollbackReservation(String reserveId, MatchingCategory category, int count) {
        String reserveKey = RESERVE_KEY.formatted(reserveId);
        List<String> reserved = basicRedisTemplate.opsForList()
                .range(reserveKey, 0, -1);

        if (reserved != null) {
            reserved.forEach(v -> {
                String[] parts = v.split(":");
                if (parts.length != 2) {
                    return;
                }

                Long memberId = Long.parseLong(parts[0]);
                double originalScore = Double.parseDouble(parts[1]);

                basicRedisTemplate.opsForZSet()
                        .add(zsetKey(category, count), memberKey(memberId), originalScore);
            });
        }

        basicRedisTemplate.delete(reserveKey);
    }

    private Optional<Long> extractMemberId(String value) {
        if (value != null && value.startsWith(MEMBER_KEY)) {
            try {
                return Optional.of(Long.parseLong(value.substring(MEMBER_KEY.length())));
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.empty();
    }

    private String zsetKey(MatchingCategory category, int participantCount) {
        return ZSET_CATEGORY_KEY.formatted(category.name(), participantCount);
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY + memberId;
    }
}
