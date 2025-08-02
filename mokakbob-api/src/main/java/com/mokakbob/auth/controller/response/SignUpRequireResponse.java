package com.mokakbob.auth.controller.response;

public record SignUpRequireResponse(
        String email,
        String nickName,
        String profileImage
) {
}
