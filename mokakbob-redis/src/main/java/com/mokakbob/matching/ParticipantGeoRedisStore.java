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

@Component
@RequiredArgsConstructor
public class ParticipantGeoRedisStore implements ParticipantGeoStore {

    private static final String GEO_KEY = "matching:geo:%s:%d";
    private static final String MEMBER_KEY = "member:";
    private static final String RESERVE_KEY = "matching:geo:reserve:%s";

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void addMemberLocation(MatchingCategory category, int count, Long memberId, double lng, double lat) {
        basicRedisTemplate.opsForGeo()
                .add(
                        geoKey(category, count),
                        new Point(lng, lat),
                        memberKey(memberId)
                );
    }

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

    @Override
    public void rollbackReservation(String reserveId, MatchingCategory category, int count) {
        String reserveKey = RESERVE_KEY.formatted(reserveId);
        List<String> reserved = basicRedisTemplate.opsForList()
                .range(reserveKey, 0, -1);

        if (reserved != null) {
            reserved.forEach(value -> {
                extractUserId(value).ifPresent(memberId -> getLocation(category, count, memberId).ifPresent(loc -> {
                    basicRedisTemplate.opsForGeo()
                            .add(
                                    geoKey(category, count),
                                    new Point(loc[1], loc[0]),
                                    memberKey(memberId)
                            );
                }));
            });
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
