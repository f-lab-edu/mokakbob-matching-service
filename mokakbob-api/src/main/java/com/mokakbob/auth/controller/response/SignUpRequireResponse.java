package com.mokakbob.auth.controller.response;

public record SignUpRequireResponse(
        String status,
        String email,
        String nickName,
        String profileImage
) {
}
