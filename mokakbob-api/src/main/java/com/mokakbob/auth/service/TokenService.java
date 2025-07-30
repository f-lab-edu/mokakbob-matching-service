package com.mokakbob.auth.service;

import com.mokakbob.auth.domain.RefreshTokenStore;
import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.auth.exception.AuthApiErrorCode;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.common.path.auth.AuthApiPath;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String COOKIE_NAME = "refreshToken";
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;

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
        cookie.setSecure(false);
        cookie.setPath(AuthApiPath.REISSUE);
        cookie.setMaxAge((int) REFRESH_TTL.getSeconds());
        response.addCookie(cookie);
    }

    public String reissue(HttpServletResponse response, HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        Long memberId = tokenProvider.extractMemberId(refreshToken);

        String savedToken = refreshTokenStore.get(memberId)
                .orElseThrow(() -> new ApiException(AuthApiErrorCode.TOKEN_NOT_FOUND));

        if (!savedToken.equals(refreshToken)) {
            throw new ApiException(AuthApiErrorCode.TOKEN_INVALID);
        }

        String newAccessToken = tokenProvider.createAccessToken(memberId);
        createRefreshToken(memberId, response);

        return newAccessToken;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new ApiException(AuthApiErrorCode.TOKEN_NOT_FOUND);
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new ApiException(AuthApiErrorCode.TOKEN_NOT_FOUND));
    }
}
