package com.mokakbob.auth.domain;

import java.time.Duration;
import java.util.Optional;

public interface EmailVerifyCodeStore {
    void saveCode(String email, String code, Duration expiration);
    Optional<String> getCode(String email);
    void deleteCode(String email);
    boolean hasCode(String email);
}
