package com.mokakbob.excpeption.response;

public record ErrorResponse(
        String customCode,
        String message
) {
}
