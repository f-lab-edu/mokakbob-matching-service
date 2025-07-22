package com.mokakbob.domain.member.domain.auth;

public interface TokenProvider {

    String create(Long memberId);
    Long extractMemberId(String token);
}
