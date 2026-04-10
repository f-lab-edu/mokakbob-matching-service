package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.auth.exception.AuthErrorCode;
import com.mokakbob.common.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final Key secretKey;
    private final long accessExpirationPeriodMillis;
    private final long refreshExpirationPeriodMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access.expiration-period}") long accessExpirationPeriodMillis,
            @Value("${jwt.refresh.expiration-period}") long refreshExpirationPeriodMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessExpirationPeriodMillis = accessExpirationPeriodMillis;
        this.refreshExpirationPeriodMillis = refreshExpirationPeriodMillis;
    }

    @Override
    public String createAccessToken(Long memberId) {
        return create(memberId, accessExpirationPeriodMillis);
    }

    @Override
    public String createRefreshToken(Long memberId) {
        return create(memberId, refreshExpirationPeriodMillis);
    }

    private String create(Long memberId, long expirationPeriodMillis) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expirationPeriodMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Long extractMemberId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    @Override
    public boolean isAccessTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);

            return claims.getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    @Override
    public Authentication getAuthentication(Long memberId) {
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        User principal = new User(
                String.valueOf(memberId),
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );
    }

    @Override
    public long getRemainingExpirationMillis(String token) {
        Date expiration = parseToken(token).getExpiration();
        Date now = new Date();
        return Math.max(0, expiration.getTime() - now.getTime());
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new ApiException(AuthErrorCode.TOKEN_EXPIRED, e);
        } catch (SignatureException e) {
            throw new ApiException(AuthErrorCode.TOKEN_INVALID_SIGNATURE, e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(AuthErrorCode.TOKEN_INVALID, e);
        }
    }
}
