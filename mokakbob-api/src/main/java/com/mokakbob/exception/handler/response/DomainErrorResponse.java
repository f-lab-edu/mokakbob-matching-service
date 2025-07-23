package com.mokakbob.exception.handler.response;

public record DomainErrorResponse(
        String customCode,
        String message
) {
}
