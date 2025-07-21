package com.mokakbob.domain.member.service.store;

import java.time.Duration;
import java.util.Optional;

public interface VerificationCodeStore {
    void saveCode(String email, String code, Duration expiration);
    Optional<String> getCode(String email);
    void deleteCode(String email);
    boolean hasCode(String email);
}
