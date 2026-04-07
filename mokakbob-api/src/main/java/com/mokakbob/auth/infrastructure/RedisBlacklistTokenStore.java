package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.BlacklistTokenStore;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisBlacklistTokenStore implements BlacklistTokenStore {

    private static final String BLACKLIST_KEY_PREFIX = "logout:";

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void save(String accessToken, Duration ttl) {
        basicRedisTemplate.opsForValue()
                .set(key(accessToken), "logout", ttl);
    }

    @Override
    public boolean exists(String accessToken) {
        return Boolean.TRUE.equals(basicRedisTemplate.hasKey(key(accessToken)));
    }

    private String key(String accessToken) {
        return BLACKLIST_KEY_PREFIX + accessToken;
    }
}
