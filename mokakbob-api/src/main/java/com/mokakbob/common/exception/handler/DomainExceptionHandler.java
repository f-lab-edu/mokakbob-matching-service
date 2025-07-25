package com.mokakbob.common.exception.handler;


import com.mokakbob.common.exception.DomainErrorCode;
import com.mokakbob.common.exception.DomainException;
import com.mokakbob.common.exception.handler.response.CustomErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<CustomErrorResponse> handleException(DomainException e) {
        DomainErrorCode baseErrorCode = e.domainErrorCode();

        CustomErrorResponse response = new CustomErrorResponse(
                baseErrorCode.customCode(),
                baseErrorCode.message()
        );

        return ResponseEntity.status(baseErrorCode.httpStatus()).body(response);
    }
}
