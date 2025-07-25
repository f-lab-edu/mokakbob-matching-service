package com.mokakbob.auth.service;

public interface TokenProvider {
    String create(Long memberId);
    Long extractMemberId(String token);
}
