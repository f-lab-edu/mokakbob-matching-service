package com.mokakbob.auth.infrastructure;

import com.mokakbob.domain.member.repository.EmailVerifyCodeStore;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisEmailVerifyCodeStore implements EmailVerifyCodeStore {

    private static final String EMAIL_KEY_PREFIX = "email:verify:";

    private final RedisTemplate<String, String> authRedisTemplate;

    @Override
    public void saveCode(String email, String code, Duration expiration) {
        String key = EMAIL_KEY_PREFIX + email;
        authRedisTemplate.opsForValue()
                .set(key, code, expiration);
    }

    @Override
    public Optional<String> getCode(String email) {
        String key = EMAIL_KEY_PREFIX + email;

        return Optional.ofNullable(authRedisTemplate.opsForValue()
                .get(key));
    }

    @Override
    public void deleteCode(String email) {
        String key = EMAIL_KEY_PREFIX + email;
        authRedisTemplate.delete(key);
    }

    @Override
    public boolean hasCode(String email) {
        String key = EMAIL_KEY_PREFIX + email;

        return Boolean.TRUE
                .equals(authRedisTemplate.hasKey(key));
    }
}
