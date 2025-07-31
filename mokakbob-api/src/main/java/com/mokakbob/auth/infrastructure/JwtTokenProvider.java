package com.mokakbob.auth.infrastructure;

import com.mokakbob.auth.domain.TokenProvider;
import com.mokakbob.auth.exception.AuthApiErrorCode;
import com.mokakbob.common.exception.exceptions.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final Key secretKey;
    private final long accessExpirationPeriod;
    private final long refreshExpirationPeriod;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access.expiration-period}") long accessExpirationPeriod,
            @Value("${jwt.refresh.expiration-period}") long refreshExpirationPeriod
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessExpirationPeriod = accessExpirationPeriod;
        this.refreshExpirationPeriod = refreshExpirationPeriod;
    }

    @Override
    public String createAccessToken(Long memberId) {
        return create(memberId, accessExpirationPeriod);
    }

    @Override
    public String createRefreshToken(Long memberId) {
        return create(memberId, refreshExpirationPeriod);
    }

    public String create(Long memberId, long expirationPeriod) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expirationPeriod);

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

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new ApiException(AuthApiErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new ApiException(AuthApiErrorCode.TOKEN_INVALID_SIGNATURE);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(AuthApiErrorCode.TOKEN_INVALID);
        }
    }
}
