package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.RefreshTokenStore;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String TOKEN_KEY_PREFIX = "refresh:";

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public void save(Long memberId, String refreshToken, Duration ttl) {
        basicRedisTemplate.opsForValue()
                .set(key(memberId), refreshToken, ttl);
    }

    @Override
    public Optional<String> get(Long memberId) {
        return Optional.ofNullable(basicRedisTemplate.opsForValue()
                .get(key(memberId)));
    }

    @Override
    public void delete(Long memberId) {
        basicRedisTemplate.delete(key(memberId));
    }

    @Override
    public boolean exists(Long memberId) {
        return Boolean.TRUE
                .equals(basicRedisTemplate.hasKey(key(memberId)));
    }

    private String key(Long memberId) {
        return TOKEN_KEY_PREFIX + memberId;
    }
}
