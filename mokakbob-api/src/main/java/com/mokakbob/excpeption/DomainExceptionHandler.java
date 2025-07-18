package com.mokakbob.excpeption;


import com.mokakbob.common.exception.DomainErrorCode;
import com.mokakbob.common.exception.DomainException;
import com.mokakbob.excpeption.response.ErrorResponse;
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
