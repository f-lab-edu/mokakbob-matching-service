package com.mokakbob.auth.filter;

import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.common.path.permit.PermitPath;
import com.mokakbob.common.util.TokenExtractor;
import com.mokakbob.global.support.AuthConstants;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rawToken = extractor.extractAccessToken(request);
        Long memberId = tokenProvider.extractMemberId(rawToken);

        Authentication authentication = tokenProvider.getAuthentication(memberId);
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        request.setAttribute(AuthConstants.TOKEN_ATTRIBUTE, memberId);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return uri.startsWith(PermitPath.AUTH_BASE) ||
                uri.startsWith(PermitPath.OAUTH_BASE) ||
                uri.startsWith(PermitPath.EMAIL_BASE)
                ;
    }
}
