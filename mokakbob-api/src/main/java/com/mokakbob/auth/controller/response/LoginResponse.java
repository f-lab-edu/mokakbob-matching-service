package com.mokakbob.auth.controller.response;

public record LoginResponse(
        Long id,
        String email,
        String nickname,
        String profileImageUrl
) {
}
