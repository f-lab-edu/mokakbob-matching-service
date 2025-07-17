package com.mokakbob.excpeption.dto;

public record ErrorResponse(
        String customCode,
        String message
) {
}
