package com.mokakbob.auth.domain;

import org.springframework.security.core.Authentication;

public interface TokenProvider {
    String createAccessToken(Long memberId);
    String createRefreshToken(Long memberId);
    Long extractMemberId(String token);
    boolean isAccessTokenExpired(String token);
    Authentication getAuthentication(Long memberId);
    long getRemainingExpirationMillis(String token);
}
