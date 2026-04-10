package com.mokakbob.auth.domain;

import java.time.Duration;

public interface BlacklistTokenStore {

    void save(String accessToken, Duration ttl);
    boolean exists(String accessToken);
}
