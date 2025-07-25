package com.mokakbob.common.exception.handler.response;

public record CustomErrorResponse(
        String customCode,
        String message
) {
}
