package com.mokakbob.member.exception;

import com.mokakbob.common.exception.exceptions.ApiErrorCode;

public enum MemberApiErrorCode implements ApiErrorCode {
    // image exception
    IMAGE_UPLOAD_FAILED(500, "I001", "이미지 업로드에 실패하였습니다."),
    IMAGE_FOLDER_CREATE_FAILED(500, "I002", "이미지 폴더 생성에 실패하였습니다."),
    IMAGE_DELETE_FAILED(500, "I003", "이미지 삭제에 실패하였습니다.")
    ;
    private final int httpStatus;
    private final String customCode;
    private final String message;

    MemberApiErrorCode(int httpStatus, String customCode, String message) {
        this.httpStatus = httpStatus;
        this.customCode = customCode;
        this.message = message;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String customCode() {
        return customCode;
    }

    @Override
    public String message() {
        return message;
    }
}
