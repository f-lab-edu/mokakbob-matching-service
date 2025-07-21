package com.mokakbob.exception.response;

public record DomainErrorResponse(
        String customCode,
        String message
) {
}
