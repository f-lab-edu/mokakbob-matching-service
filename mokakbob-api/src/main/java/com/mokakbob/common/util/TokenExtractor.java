package com.mokakbob.common.util;

import com.mokakbob.auth.exception.AuthErrorCode;
import com.mokakbob.common.exception.ApiException;
import com.mokakbob.common.constant.AuthConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {


    public String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader(AuthConstants.AUTH_HEADER);
        if (header == null || !header.startsWith(AuthConstants.BEARER_PREFIX)) {
            throw new ApiException(AuthErrorCode.TOKEN_INVALID);
        }
        return header.substring(AuthConstants.BEARER_PREFIX.length());
    }

    public String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new ApiException(AuthErrorCode.TOKEN_NOT_FOUND);
        }

        return Arrays.stream(request.getCookies())
                .filter(c -> AuthConstants.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new ApiException(AuthErrorCode.TOKEN_NOT_FOUND));
    }
}
