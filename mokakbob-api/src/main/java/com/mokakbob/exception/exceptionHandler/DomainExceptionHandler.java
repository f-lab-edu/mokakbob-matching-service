package com.mokakbob.exception.exceptionHandler;


import com.mokakbob.common.exception.DomainErrorCode;
import com.mokakbob.common.exception.DomainException;
import com.mokakbob.exception.response.DomainErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<DomainErrorResponse> handleException(DomainException e) {
        DomainErrorCode baseErrorCode = e.domainErrorCode();

        DomainErrorResponse response = new DomainErrorResponse(
                baseErrorCode.customCode(),
                baseErrorCode.message()
        );

        return ResponseEntity.status(baseErrorCode.httpStatus()).body(response);
    }
}
