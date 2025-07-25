package com.mokakbob.common.exception.handler;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;
import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.common.exception.handler.response.CustomErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CustomErrorResponse> handleException(ApiException e) {
        ApiErrorCode baseErrorCode = e.apiErrorCode();

        CustomErrorResponse response = new CustomErrorResponse(
                baseErrorCode.customCode(),
                baseErrorCode.message()
        );

        return ResponseEntity.status(baseErrorCode.httpStatus())
                .body(response);
    }
}
