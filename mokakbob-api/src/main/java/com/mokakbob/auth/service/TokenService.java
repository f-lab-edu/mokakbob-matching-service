package com.mokakbob.auth.service;

import com.mokakbob.auth.domain.BlacklistTokenStore;
import com.mokakbob.auth.domain.RefreshTokenStore;
import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.auth.exception.AuthErrorCode;
import com.mokakbob.common.exception.ApiException;
import com.mokakbob.common.constant.AuthConstants;
import com.mokakbob.common.path.auth.AuthPath;
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

    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final BlacklistTokenStore blacklistTokenStore;
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
        Cookie cookie = new Cookie(AuthConstants.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(AuthPath.REISSUE);
        cookie.setMaxAge((int) REFRESH_TTL.getSeconds());
        response.addCookie(cookie);
    }

    public void logout(HttpServletRequest request) {
        String accessToken = extractor.extractAccessToken(request);
        Long memberId = tokenProvider.extractMemberId(accessToken);

        // 리프레시 토큰 삭제
        refreshTokenStore.delete(memberId);

        // 액세스 토큰 블랙리스트 등록
        long remainingExpirationMillis = tokenProvider.getRemainingExpirationMillis(accessToken);
        blacklistTokenStore.save(accessToken, Duration.ofMillis(remainingExpirationMillis));
    }

    public boolean isBlacklisted(String accessToken) {
        return blacklistTokenStore.exists(accessToken);
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
            throw new ApiException(AuthErrorCode.TOKEN_NOT_EXPIRED);
        }
    }

    private void validateRefreshToken(String refreshToken, Long memberId) {
        String savedToken = refreshTokenStore.get(memberId)
                .orElseThrow(() -> new ApiException(AuthErrorCode.TOKEN_NOT_FOUND));

        if (!savedToken.equals(refreshToken)) {
            throw new ApiException(AuthErrorCode.TOKEN_INVALID);
        }
    }
}
