package com.mokakbob.common.exception.exceptions;

import org.springframework.http.HttpStatus;

public interface DomainErrorCode {

    HttpStatus httpStatus();

    String customCode();

    String message();
}
