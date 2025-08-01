package com.mokakbob.global.filter;

import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.common.path.auth.AuthApiPath;
import com.mokakbob.common.path.auth.EmailApiPath;
import com.mokakbob.common.util.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_INFORMATION = "memberId";

    private final TokenExtractor extractor;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rawToken = extractor.extractAccessToken(request);
        Long memberId = tokenProvider.extractMemberId(rawToken);
        request.setAttribute(TOKEN_INFORMATION, memberId);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return uri.startsWith(AuthApiPath.BASE) ||
                uri.startsWith(EmailApiPath.BASE);
    }
}
