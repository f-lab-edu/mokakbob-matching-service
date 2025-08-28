package com.mokakbob.common.exception.handler;

import com.mokakbob.common.exception.handler.response.CustomErrorResponse;
import com.mokakbob.matching.common.exception.exceptions.ConsumerErrorCode;
import com.mokakbob.matching.common.exception.exceptions.ConsumerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ConsumerExceptionHandler {

    @ExceptionHandler(ConsumerException.class)
    public ResponseEntity<CustomErrorResponse> handleException(ConsumerException e) {
        ConsumerErrorCode baseErrorCode = e.consumerErrorCode();

        CustomErrorResponse response = new CustomErrorResponse(
                baseErrorCode.customCode(),
                baseErrorCode.message()
        );

        return ResponseEntity.status(baseErrorCode.httpStatus())
                .body(response);
    }
}
