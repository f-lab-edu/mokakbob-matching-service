package com.mokakbob.auth.controller.response;

public record LoginResponse(
        String token,
        Long id,
        String email,
        String nickname,
        String profileImageUrl
) {
}
