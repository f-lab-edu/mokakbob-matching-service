package com.mokakbob.auth.handler.response;

public record SignUpRequireResponse(
        boolean signUpStatus,
        String email,
        String nickName,
        String profileImage
) {
}
