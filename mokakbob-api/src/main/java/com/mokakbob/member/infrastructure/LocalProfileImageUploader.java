package com.mokakbob.member.infrastructure;

import com.mokakbob.member.domain.ProfileImageUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalProfileImageUploader implements ProfileImageUploader {

    @Value("${local.upload.profile.directory}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file, String directory) {
        return "";
    }

    @Override
    public void delete(String filePath) {

    }
}
