package com.mokakbob.auth.controller.response;

public record LoginResponse(
        String accessToken,
        Long id,
        String email,
        String nickname,
        String profileImageUrl
) {
}
