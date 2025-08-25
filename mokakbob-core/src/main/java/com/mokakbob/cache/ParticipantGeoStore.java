package com.mokakbob.cache;

import com.mokakbob.domain.matching.domain.vo.MatchingCategory;
import java.util.List;
import java.util.Optional;

public interface ParticipantGeoStore {

    void addMemberLocation(MatchingCategory category, int count, Long memberId, double lng, double lat);
    List<Long> findNearbyMembers(MatchingCategory category, int count, double lng, double lat, double radiusInMeters);
    void removeMemberLocation(MatchingCategory category, int count, Long memberId);
    Optional<double[]> getLocation(MatchingCategory category, int count, Long memberId);
}
