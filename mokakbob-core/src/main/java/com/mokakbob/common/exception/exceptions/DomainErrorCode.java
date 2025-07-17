package com.mokakbob.common.exception.exceptions;

public interface DomainErrorCode {

    int httpStatus();

    String customCode();

    String message();
}
