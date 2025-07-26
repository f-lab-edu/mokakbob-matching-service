package com.mokakbob.auth.controller.request;

import com.mokakbob.domain.member.domain.vo.MemberPreference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

        @NotNull
        MemberPreference preference
) {
}
