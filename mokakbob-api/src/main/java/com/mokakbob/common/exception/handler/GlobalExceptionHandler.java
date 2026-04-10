package com.mokakbob.common.exception.handler;

import com.mokakbob.common.exception.ApiErrorCode;
import com.mokakbob.common.exception.BaseErrorCode;
import com.mokakbob.common.exception.BaseException;
import com.mokakbob.common.exception.handler.response.CustomErrorResponse;
import com.mokakbob.common.exception.handler.response.ValidateErrorResponse;
import com.mokakbob.domain.exception.DomainErrorCode;
import com.mokakbob.domain.exception.DomainException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 도메인 예외를 처리합니다.
     * 각 DomainErrorCode가 자신의 httpStatus를 직접 갖고 있으므로 분기 로직이 불필요합니다.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<CustomErrorResponse> handleDomainException(DomainException e) {
        DomainErrorCode errorCode = e.getErrorCode();
        log.warn("Domain Exception: [{} - {}]", errorCode.getCustomCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new CustomErrorResponse(errorCode.getCustomCode(), errorCode.getMessage()));
    }

    /**
     * 프로젝트 내 모든 커스텀 예외(BaseException 상속)를 통합 처리합니다.
     * DomainException은 위 핸들러가 우선 처리하므로 여기는 Api/Redis/Consumer 예외만 도달합니다.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CustomErrorResponse> handleBaseException(BaseException e) {
        return handleExceptionInternal(e.getErrorCode());
    }

    private ResponseEntity<CustomErrorResponse> handleExceptionInternal(BaseErrorCode errorCode) {
        int status;

        if (errorCode instanceof ApiErrorCode apiError) {
            log.warn("API Exception: [{} - {}]", errorCode.getCustomCode(), errorCode.getMessage());
            status = apiError.getHttpStatus();
        } else {
            // Redis/Consumer 등 httpStatus가 없는 예외는 서버 오류
            log.error("Unhandled Exception: [{} - {}]", errorCode.getCustomCode(), errorCode.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        return ResponseEntity.status(status)
                .body(new CustomErrorResponse(errorCode.getCustomCode(), errorCode.getMessage()));
    }

    /**
     * 컨트롤러 입력값 검증 실패 시 에러를 처리합니다.
     * 보안을 위해 필드명만 로깅하고 rejected value는 로그에서 제외합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidateErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation Failed: fields={}",
                ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getField)
                        .toList());

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
