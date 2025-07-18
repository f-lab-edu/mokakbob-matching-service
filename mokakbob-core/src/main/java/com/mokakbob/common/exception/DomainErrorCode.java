package com.mokakbob.common.exception;

public interface DomainErrorCode {

    int httpStatus();

    String customCode();

    String message();
}
