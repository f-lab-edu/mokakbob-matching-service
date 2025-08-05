package com.mokakbob.auth.service;

import com.mokakbob.auth.domain.RefreshTokenStore;
import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.auth.exception.AuthApiErrorCode;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.common.util.TokenExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String COOKIE_NAME = "refreshToken";
    private static final Duration REFRESH_TTL = Duration.ofDays(7);
    private static final String REISSUE_API_PATH = "/api/v1/auth/reissue";

    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final TokenExtractor extractor;

    public String createAccessToken(Long memberId) {
        return tokenProvider.createAccessToken(memberId);
    }

    public void createRefreshToken(Long memberId, HttpServletResponse response) {
        String refreshToken = tokenProvider.createRefreshToken(memberId);
        refreshTokenStore.save(memberId, refreshToken, REFRESH_TTL);
        addRefreshTokenToCookie(response, refreshToken);
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(REISSUE_API_PATH);
        cookie.setMaxAge((int) REFRESH_TTL.getSeconds());
        response.addCookie(cookie);
    }

    public String reissue(HttpServletResponse response, HttpServletRequest request) {
        validateAccessToken(request);

        String refreshToken = extractor.extractRefreshToken(request);
        Long memberId = tokenProvider.extractMemberId(refreshToken);
        validateRefreshToken(refreshToken, memberId);

        String newAccessToken = tokenProvider.createAccessToken(memberId);
        createRefreshToken(memberId, response);

        return newAccessToken;
    }

    private void validateAccessToken(HttpServletRequest request) {
        String accessToken = extractor.extractAccessToken(request);
        if (!tokenProvider.isAccessTokenExpired(accessToken)) {
            throw new ApiException(AuthApiErrorCode.TOKEN_NOT_EXPIRED);
        }
    }

    private void validateRefreshToken(String refreshToken, Long memberId) {
        String savedToken = refreshTokenStore.get(memberId)
                .orElseThrow(() -> new ApiException(AuthApiErrorCode.TOKEN_NOT_FOUND));

        if (!savedToken.equals(refreshToken)) {
            throw new ApiException(AuthApiErrorCode.TOKEN_INVALID);
        }
    }
}
