package com.mokakbob.auth.filter;

import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.auth.service.TokenService;
import com.mokakbob.common.exception.ApiException;
import com.mokakbob.auth.exception.AuthErrorCode;
import com.mokakbob.common.path.permit.PermitPath;
import com.mokakbob.common.util.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractor extractor;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rawToken = extractor.extractAccessToken(request);

        if (tokenService.isBlacklisted(rawToken)) {
            throw new ApiException(AuthErrorCode.LOGOUT_ACCESS_TOKEN);
        }

        Long memberId = tokenProvider.extractMemberId(rawToken);

        Authentication authentication = tokenProvider.getAuthentication(memberId);
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return uri.startsWith(PermitPath.AUTH_BASE) ||
                uri.startsWith(PermitPath.EMAIL_BASE) ||
                uri.startsWith(PermitPath.ADMIN_BASE) ||
                uri.startsWith("/actuator") || // 헬스체크 bypass
                uri.startsWith("/ws-connect")  // 웹소켓
                ;
    }
}
