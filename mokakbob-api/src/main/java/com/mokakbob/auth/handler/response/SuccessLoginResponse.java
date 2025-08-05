package com.mokakbob.auth.handler.response;

public record SuccessLoginResponse(
        boolean loginStatus,
        Long memberId,
        String email,
        String nickname,
        String profileImage
) {
}
