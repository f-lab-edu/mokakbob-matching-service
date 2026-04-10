package com.mokakbob.matching.common.exception;

import com.mokakbob.common.exception.BaseErrorCode;

public interface ConsumerErrorCode extends BaseErrorCode {
    boolean isRetryable();
}
