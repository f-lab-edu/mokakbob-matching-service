package com.mokakbob.matching;

import com.mokakbob.cache.ParticipantGeoStore;
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

    private static final String GEO_KEY = "matching:geo";
    private static final String MEMBER_KEY = "member:";

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void addMemberLocation(Long memberId, double lng, double lat) {
        String member = memberKey(memberId);

        basicRedisTemplate.opsForGeo()
                .add(GEO_KEY, new Point(lng, lat), member);
    }

    @Override
    public List<Long> findNearbyMembers(double lng, double lat, double radiusInMeters) {
        GeoResults<GeoLocation<String>> results = basicRedisTemplate.opsForGeo()
                .radius(
                        GEO_KEY,
                        new Circle(new Point(lng, lat), new Distance(radiusInMeters, Metrics.METERS))
                );

        if (results == null) {
            return Collections.emptyList();
        }

        return results.getContent().stream()
                .map(result -> extractUserId(result.getContent().getName()))
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void removeMemberLocation(Long memberId) {
        basicRedisTemplate.opsForGeo()
                .remove(GEO_KEY, memberKey(memberId));
    }

    private String memberKey(Long memberId) {
        return MEMBER_KEY + memberId;
    }

    private Optional<Long> extractUserId(String value) {
        if (value != null && value.startsWith(MEMBER_KEY)) {
            try {
                return Optional.of(Long.parseLong(value.substring(5)));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
