package com.mokakbob.matching;

import com.mokakbob.cache.ParticipantGeoStore;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Component;

/**
 * Redis GEO 기반 참가자 저장소 구현체.
 * <p>
 * 주요 기능: - GEO 자료구조를 사용하여 카테고리/인원 수 단위로 참가자 위치를 관리 - 특정 반경 내 근처 멤버 탐색 - 예약(reserve)/롤백(rollback)을 통해 멤버를 임시로 제외 및 복원
 * 가능
 * <p>
 * Redis 구조: - GEO_KEY : GEO (카테고리+인원 단위로 멤버 위치 관리) - MEMBER_KEY : "member:{id}" 형태로 멤버 ID 저장 - RESERVE_KEY : List
 * (reserveId 단위로 임시 예약 멤버 보관)
 */
@Component
@RequiredArgsConstructor
public class ParticipantGeoRedisStore implements ParticipantGeoStore {

    private static final String GEO_KEY = "matching:geo:%s:%d";
    private static final String MEMBER_KEY = "member:";
    private static final String RESERVE_KEY = "matching:geo:reserve:%s";

    private final RedisTemplate<String, String> basicRedisTemplate;

    /**
     * 참가자의 위치를 GEO에 추가한다.
     *
     * @param category 매칭 카테고리
     * @param count    필요 인원 수
     * @param memberId 참가자 ID
     * @param lng      경도 (longitude)
     * @param lat      위도 (latitude)
     */
    @Override
    public void addMemberLocation(MatchingCategory category, int count, Long memberId, double lng, double lat) {
        basicRedisTemplate.opsForGeo()
                .add(
                        geoKey(category, count),
                        new Point(lng, lat),
                        memberKey(memberId)
                );
    }

    /**
     * 특정 위치를 중심으로 반경 내 참가자들을 조회한다.
     *
     * @param category       매칭 카테고리
     * @param count          필요 인원 수
     * @param lng            중심 경도
     * @param lat            중심 위도
     * @param radiusInMeters 탐색 반경 (미터 단위)
     * @return 반경 내 참가자 ID 리스트
     */
    @Override
    public List<Long> findNearbyMembers(MatchingCategory category, int count, double lng, double lat,
                                        double radiusInMeters) {
        GeoResults<GeoLocation<String>> results = basicRedisTemplate.opsForGeo()
                .radius(
                        geoKey(category, count),
                        new Circle(new Point(lng, lat), new Distance(radiusInMeters, Metrics.METERS))
                );

        if (results == null || results.getContent().isEmpty()) {
            return Collections.emptyList();
        }

        return results.getContent().stream()
                .map(r -> extractUserId(r.getContent().getName()))
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void removeMemberLocation(MatchingCategory category, int count, Long memberId) {
        basicRedisTemplate.opsForGeo()
                .remove(geoKey(category, count), memberKey(memberId));
    }

    @Override
    public Optional<double[]> getLocation(MatchingCategory category, int count, Long memberId) {
        List<Point> points = basicRedisTemplate.opsForGeo()
                .position(geoKey(category, count), memberKey(memberId));

        if (points == null || points.isEmpty() || points.get(0) == null) {
            return Optional.empty();
        }

        Point p = points.get(0);

        return Optional.of(new double[]{p.getY(), p.getX()});
    }

    /**
     * 특정 참가자를 GEO에서 제거하고 예약 상태로 옮긴다.
     *
     * @param category  매칭 카테고리
     * @param count     필요 인원 수
     * @param memberId  예약할 멤버 ID
     * @param reserveId 예약 식별자
     * @param ttl       예약 유지 시간 (TTL)
     * @return true  : 예약 성공 false : 이미 다른 매칭에서 제거된 경우
     * <p>
     * 동작:
     * 1. GEO에서 해당 멤버 제거
     * 2. 예약 리스트("matching:geo:reserve:{reserveId}")에 저장 3. TTL 설정 (자동 만료 방지)
     */
    @Override
    public boolean reserveMember(MatchingCategory category, int count, Long memberId, String reserveId, Duration ttl) {
        String geoKey = geoKey(category, count);
        String memberKey = memberKey(memberId);

        Long removed = basicRedisTemplate.opsForGeo()
                .remove(geoKey, memberKey);

        if (removed == null || removed == 0) {
            return false;
        }

        String reserveKey = RESERVE_KEY.formatted(reserveId);
        basicRedisTemplate.opsForList()
                .rightPush(reserveKey, memberKey);

        basicRedisTemplate.expire(reserveKey, ttl);

        return true;
    }

    /**
     * 예약된 참가자들을 GEO로 복원한다.
     *
     * @param reserveId 예약 식별자
     * @param category  매칭 카테고리
     * @param count     필요 인원 수
     * <p>
     * 동작:
     * 1. 예약 리스트("matching:geo:reserve:{reserveId}")를 조회
     * 2. 각 멤버를 GEO에 다시 추가
     * 3. 예약 리스트 삭제
     */
    @Override
    public void rollbackReservation(String reserveId, MatchingCategory category, int count) {
        String reserveKey = RESERVE_KEY.formatted(reserveId);
        List<String> reserved = basicRedisTemplate.opsForList()
                .range(reserveKey, 0, -1);

        if (reserved != null) {
            reserved.forEach(value ->
                    extractUserId(value).ifPresent(memberId ->
                            getLocation(category, count, memberId).ifPresent(loc ->
                                    basicRedisTemplate.opsForGeo().add(
                                            geoKey(category, count),
                                            new Point(loc[1], loc[0]),
                                            memberKey(memberId)
                                    )
                            )
                    )
            );
        }

        basicRedisTemplate.delete(reserveKey);
    }

    private String geoKey(MatchingCategory category, int count) {
        return GEO_KEY.formatted(category.name(), count);
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY + memberId;
    }

    private Optional<Long> extractUserId(String value) {
        if (value != null && value.startsWith(MEMBER_KEY)) {
            try {
                return Optional.of(Long.parseLong(value.substring(MEMBER_KEY.length())));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
