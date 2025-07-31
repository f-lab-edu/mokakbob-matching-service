package com.mokakbob.member.domain;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageUploader {
    String upload(MultipartFile file, String directory);
    void delete(String filePath);
}
