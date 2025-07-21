package com.mokakbob.member.controller.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailSendRequest(
        @NotBlank
        @Email
        String email
) {
}
