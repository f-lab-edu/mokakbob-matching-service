package com.mokakbob.auth.domain;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenStore {

    void save(Long memberId, String refreshToken, Duration ttl);
    Optional<String> get(Long memberId);
    void delete(Long memberId);
    boolean exists(Long memberId);
}
