package com.mokakbob.member.exception;

import com.mokakbob.common.exception.BaseErrorCode;

public enum MemberApiErrorCode implements BaseErrorCode {
    // image exception
    IMAGE_UPLOAD_FAILED(500, "I001", "이미지 업로드에 실패하였습니다."),
    IMAGE_FOLDER_CREATE_FAILED(500, "I002", "이미지 폴더 생성에 실패하였습니다."),
    IMAGE_DELETE_FAILED(500, "I003", "이미지 삭제에 실패하였습니다."),
    IMAGE_EMPTY(400, "I004", "올바른 이미지를 첨부해주세요"),
    WRONG_IMAGE_NAME(400, "I005", "파일 이름이 유효하지 않습니다."),
    WRONG_IMAGE_EXTENSION(400, "I006", "허용되지 않는 확장자입니다."),
    WRONG_IMAGE_MIME(400, "I007", "허용되지 않는 MIME type 입니다."),
    IMAGE_VOLUME(400, "I008", "이미지 최대 용량은 5MB 입니다.")
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
