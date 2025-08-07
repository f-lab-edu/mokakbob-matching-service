package com.mokakbob.matching.domain;

import java.util.List;

public interface ParticipantGeoStore {

    void addMemberLocation(Long memberId, double lng, double lat);
    List<Long> findNearbyMembers(double lng, double lat, double radiusInMeters);
    void removeMemberLocation(Long memberId);
}
