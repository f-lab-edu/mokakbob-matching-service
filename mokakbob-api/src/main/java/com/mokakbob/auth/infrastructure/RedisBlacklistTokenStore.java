package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.BlacklistTokenStore;
import com.mokakbob.config.RedisConstants;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisBlacklistTokenStore implements BlacklistTokenStore {

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void save(String accessToken, Duration ttl) {
        Objects.requireNonNull(accessToken, "accessToken must not be null");
        Objects.requireNonNull(ttl, "ttl must not be null");
        
        basicRedisTemplate.opsForValue()
                .set(key(accessToken), RedisConstants.BLACKLIST_VALUE, ttl);
    }

    @Override
    public boolean exists(String accessToken) {
        Objects.requireNonNull(accessToken, "accessToken must not be null");
        Boolean result = basicRedisTemplate.hasKey(key(accessToken));
        return Boolean.TRUE.equals(result);
    }

    @NonNull
    private String key(@NonNull String accessToken) {
        return RedisConstants.BLACKLIST_KEY_PREFIX + accessToken;
    }
}
