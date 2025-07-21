package com.mokakbob.member.auth;

import com.mokakbob.domain.member.repository.EmailVerifyCodeStore;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisEmailVerifyCodeStore implements EmailVerifyCodeStore {

    private final RedisTemplate<String, String> authRedisTemplate;

    @Override
    public void saveCode(String email, String code, Duration expiration) {

    }

    @Override
    public Optional<String> getCode(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteCode(String email) {

    }

    @Override
    public boolean hasCode(String email) {
        return false;
    }
}
