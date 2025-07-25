package com.mokakbob.auth.domain;

public interface TokenProvider {
    String create(Long memberId);
    Long extractMemberId(String token);
}
