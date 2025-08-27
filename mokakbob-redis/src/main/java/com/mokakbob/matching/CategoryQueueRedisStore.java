package com.mokakbob.matching;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import com.mokakbob.cache.CategoryQueueStore;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 카테고리별 매칭 대기열을 Redis 기반으로 관리하는 구현체.
 * <p>
 * Redis 구조
 * - ZSET_CATEGORY_KEY : ZSet (카테고리+참여인원 단위로 대기열 관리, score = 대기 시작 시간)
 * - RESERVE_KEY       : List (reserveId 단위로 임시 예약 멤버 관리, 원복 가능)
 */
@Component
@RequiredArgsConstructor
public class CategoryQueueRedisStore implements CategoryQueueStore {

    private static final String ZSET_CATEGORY_KEY = "matching:zset:%s:%d";
    private static final String MEMBER_KEY = "member:";
    private static final String RESERVE_KEY = "matching:reserve:%s";

    private final RedisTemplate<String, String> basicRedisTemplate;

    /**
     * 매칭 대기열에 새로운 멤버를 추가한다.
     *
     * @param category 매칭 카테고리
     * @param count    필요한 참가자 수
     * @param memberId 대기열에 추가할 멤버 ID
     * <p>
     * Redis ZSET 사용:
     * - key : "matching:zset:{category}:{count}"
     * - value : "member:{memberId}"
     * - score : System.currentTimeMillis() (대기 시작 시각)
     */
    @Override
    public void addToQueue(MatchingCategory category, int count, Long memberId) {
        String zsetKey = zsetKey(category, count);
        double score = System.currentTimeMillis();

        basicRedisTemplate.opsForZSet()
                .add(zsetKey, memberKey(memberId), score);
    }

    /**
     * 매칭 대기열에서 특정 멤버를 제거한다.
     *
     * @param category 매칭 카테고리
     * @param count    필요한 참가자 수
     * @param memberId 제거할 멤버 ID
     *
     * Redis ZSET에서 해당 멤버를 삭제한다.
     */
    @Override
    public void removeFromQueue(MatchingCategory category, int count, Long memberId) {
        String zsetKey = zsetKey(category, count);

        basicRedisTemplate.opsForZSet()
                .remove(zsetKey, memberKey(memberId));
    }

    /**
     * 해당 카테고리/인원 수 기준으로 매칭을 시도할 수 있는지 확인한다.
     *
     * @param category 매칭 카테고리
     * @param participantCount 필요한 참가자 수
     * @return true  : 현재 큐에 참가자가 충분함
     *         false : 참가자가 부족함
     *
     * Redis ZSET의 cardinality(원소 개수)를 조회한다.
     */
    @Override
    public boolean hasEnoughForMatching(MatchingCategory category, int participantCount) {
        Long existMembers = basicRedisTemplate.opsForZSet()
                .zCard(zsetKey(category, participantCount));

        return existMembers != null && existMembers >= participantCount;
    }

    /**
     * 특정 멤버를 대기열에서 꺼내 예약 상태로 이동시킨다.
     *
     * @param category 매칭 카테고리
     * @param count    필요한 참가자 수
     * @param memberId 예약할 멤버 ID
     * @param reserveId 예약 식별자
     * @param ttl      예약 보관 TTL
     * @return true  : 예약 성공
     *         false : 이미 다른 매칭에서 꺼내간 경우
     * <p>
     * 동작:
     * 1. ZSET에서 해당 멤버의 원래 score 조회
     * 2. 멤버 제거(remove) 시도 → 성공하면 예약 리스트("matching:reserve:{reserveId}")에 기록
     * 3. TTL 설정
     */
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

    /**
     * 예약을 취소(rollback)한다.
     *
     * @param reserveId 예약 식별자
     * @param category  매칭 카테고리
     * @param count     참가자 수
     * <p>
     * 동작:
     * 1. 예약 리스트("matching:reserve:{reserveId}")를 읽어 멤버 ID와 originalScore 복원
     * 2. 다시 ZSET에 추가
     * 3. 예약 리스트 삭제
     */
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

    private String zsetKey(MatchingCategory category, int participantCount) {
        return ZSET_CATEGORY_KEY.formatted(category.name(), participantCount);
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY + memberId;
    }
}
