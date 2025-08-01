package com.mokakbob.member.controller.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ImageRequest(
        @NotNull
        MultipartFile file
) {
}
