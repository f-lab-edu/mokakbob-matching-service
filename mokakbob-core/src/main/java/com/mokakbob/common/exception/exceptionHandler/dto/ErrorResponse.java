package com.mokakbob.common.exception.exceptionHandler.dto;

public record ErrorResponse(
        String customCode,
        String message
) {
}
