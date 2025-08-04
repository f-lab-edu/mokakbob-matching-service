package com.mokakbob.global.filter;

import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.common.path.auth.AuthEmailPath;
import com.mokakbob.common.path.auth.AuthPath;
import com.mokakbob.common.path.auth.OauthPath;
import com.mokakbob.common.util.TokenExtractor;
import com.mokakbob.global.support.AuthConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractor extractor;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rawToken = extractor.extractAccessToken(request);
        Long memberId = tokenProvider.extractMemberId(rawToken);
        request.setAttribute(AuthConstants.TOKEN_ATTRIBUTE, memberId);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return uri.startsWith(AuthPath.BASE) ||
                uri.startsWith(OauthPath.BASE) ||
                uri.startsWith(AuthEmailPath.BASE)
                ;
    }
}
