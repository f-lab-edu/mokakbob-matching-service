package com.mokakbob.common.exception.exceptionHandler;

import com.mokakbob.common.exception.exceptionHandler.dto.ErrorResponse;
import com.mokakbob.common.exception.exceptions.DomainErrorCode;
import com.mokakbob.common.exception.exceptions.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleException(DomainException e) {
        DomainErrorCode baseErrorCode = e.domainErrorCode();

        ErrorResponse response = new ErrorResponse(
                baseErrorCode.customCode(),
                baseErrorCode.message()
        );

        return ResponseEntity.status(baseErrorCode.httpStatus()).body(response);
    }
}
