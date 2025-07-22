package com.mokakbob.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record SignUpRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 5, max = 20)
        String password,

        @NotBlank
        @Size(max = 20)
        String nickName,

        @NotBlank
        String preference,

        MultipartFile profileImage
) {
}
