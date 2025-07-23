package com.mokakbob.exception.handler.response;

import java.util.List;

public record ValidateErrorResponse(
        List<FieldError> errors
) {
    public static ValidateErrorResponse of(List<FieldError> errors) {
        return new ValidateErrorResponse(errors);
    }

    public record FieldError(
            String field,
            String reason
    ) {
    }
}
