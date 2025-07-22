package com.mokakbob.domain.member.infrastructure;

import com.mokakbob.common.exception.DomainException;
import com.mokakbob.domain.member.domain.auth.TokenProvider;
import com.mokakbob.domain.member.exception.MemberErrorCode;
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
    private final long expirationPeriod;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-period}") long expirationPeriod
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationPeriod = expirationPeriod;
    }

    @Override
    public String create(Long memberId) {
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

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new DomainException(MemberErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new DomainException(MemberErrorCode.TOKEN_INVALID_SIGNATURE);
        } catch (JwtException | IllegalArgumentException e) {
            throw new DomainException(MemberErrorCode.TOKEN_INVALID);
        }
    }
}
