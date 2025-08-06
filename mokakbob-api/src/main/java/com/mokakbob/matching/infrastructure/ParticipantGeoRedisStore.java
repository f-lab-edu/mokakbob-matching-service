package com.mokakbob.matching.infrastructure;

import com.mokakbob.matching.domain.ParticipantGeoStore;
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

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void addUserLocation(Long userId, double lng, double lat) {
        String member = userKey(userId);

        basicRedisTemplate.opsForGeo()
                .add(GEO_KEY, new Point(lng, lat), member);
    }

    @Override
    public List<Long> findNearbyUsers(double lng, double lat, double radiusInMeters) {
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
    public void removeUserLocation(Long userId) {
        basicRedisTemplate.opsForZSet()
                .remove(GEO_KEY, userKey(userId));
    }

    private String userKey(Long userId) {
        return "user:" + userId;
    }

    private Optional<Long> extractUserId(String value) {
        if (value != null && value.startsWith("user:")) {
            try {
                return Optional.of(Long.parseLong(value.substring(5)));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
