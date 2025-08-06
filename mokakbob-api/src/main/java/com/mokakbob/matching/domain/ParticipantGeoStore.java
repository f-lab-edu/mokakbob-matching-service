package com.mokakbob.matching.domain;

import java.util.List;

public interface ParticipantGeoStore {

    void addUserLocation(Long userId, double lng, double lat);
    List<Long> findNearbyUsers(double lng, double lat, double radiusInMeters);
    void removeUserLocation(Long userId);
}
