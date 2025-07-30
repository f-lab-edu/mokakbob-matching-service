package com.mokakbob.auth.service;

import com.mokakbob.auth.domain.RefreshTokenStore;
import com.mokakbob.auth.domain.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String COOKIE_NAME = "refreshToken";
    private static final int COOKIE_DURATION = 7 * 24 * 60 * 60;
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public String createAccessToken(Long memberId) {
        return tokenProvider.createAccessToken(memberId);
    }

    public String createRefreshToken(Long memberId) {
        String refreshToken = tokenProvider.createRefreshToken(memberId);
        refreshTokenStore.save(memberId, refreshToken, REFRESH_TTL);

        return refreshToken;
    }

    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/token/reissue");
        cookie.setMaxAge(COOKIE_DURATION);
        response.addCookie(cookie);
    }
}
