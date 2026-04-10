package com.mokakbob.member.exception;

import com.mokakbob.common.exception.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberApiErrorCode implements ApiErrorCode {
    ALREADY_EXIST_MEMBER(409, "MEM001", "이미 존재하는 회원입니다."),
    NOT_FOUND_MEMBER(404, "MEM002", "회원을 찾을 수 없습니다."),
    INVALID_PASSWORD(401, "MEM003", "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_LOCATION(404, "MEM004", "회원의 위치 정보를 찾을 수 없습니다."),

    WRONG_IMAGE_NAME(400, "MEM005", "잘못된 이미지 파일명입니다."),
    WRONG_IMAGE_EXTENSION(400, "MEM006", "지원하지 않는 이미지 확장자입니다."),
    WRONG_IMAGE_MIME(400, "MEM007", "지원하지 않는 이미지 타입입니다."),
    IMAGE_VOLUME(400, "MEM008", "이미지 용량이 너무 큽니다."),
    IMAGE_UPLOAD_FAILED(500, "MEM009", "이미지 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(500, "MEM010", "이미지 삭제에 실패했습니다."),
    IMAGE_FOLDER_CREATE_FAILED(500, "MEM011", "이미지 저장 디렉토리 생성에 실패했습니다."),
    IMAGE_EMPTY(400, "MEM012", "이미지 파일이 비어있습니다."),
    ;

    private final int httpStatus;
    private final String customCode;
    private final String message;
}
