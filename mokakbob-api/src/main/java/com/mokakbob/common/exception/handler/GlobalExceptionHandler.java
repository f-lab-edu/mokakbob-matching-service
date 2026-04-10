package com.mokakbob.common.exception.handler;

import com.mokakbob.common.exception.ApiErrorCode;
import com.mokakbob.common.exception.BaseErrorCode;
import com.mokakbob.common.exception.BaseException;
import com.mokakbob.common.exception.handler.response.CustomErrorResponse;
import com.mokakbob.common.exception.handler.response.ValidateErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 프로젝트 내 모든 커스텀 예외(BaseException 상속)를 통합 처리합니다.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CustomErrorResponse> handleBaseException(BaseException e) {
        return handleExceptionInternal(e.getErrorCode());
    }

    private ResponseEntity<CustomErrorResponse> handleExceptionInternal(BaseErrorCode errorCode) {
        log.warn("Exception Occurred: [{} - {}]", errorCode.getCustomCode(), errorCode.getMessage());

        int status = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (errorCode instanceof ApiErrorCode) {
            status = ((ApiErrorCode) errorCode).getHttpStatus();
        }

        return ResponseEntity.status(status)
                .body(new CustomErrorResponse(errorCode.getCustomCode(), errorCode.getMessage()));
    }

    /**
     * 컨트롤러 입력값 검증 실패 시 에러를 처리합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidateErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation Failed: {}", ex.getMessage());
        List<ValidateErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ValidateErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.badRequest()
                .body(ValidateErrorResponse.of(fieldErrors));
    }
}
