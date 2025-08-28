package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.vo.Location;
import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface ParticipantGeoStore {

    void addMemberLocation(MatchingCategory category, int count, Long memberId, Location location);
    List<Long> findNearbyMembers(MatchingCategory category, int count, Location location, double radiusInMeters);
    void removeMemberLocation(MatchingCategory category, int count, Long memberId);
    Optional<Location> getLocation(MatchingCategory category, int count, Long memberId);
    boolean reserveMember(MatchingCategory category, int count, Long memberId, String reserveId, Duration ttl);
    void rollbackReservation(String reserveId, MatchingCategory category, int count);
}
