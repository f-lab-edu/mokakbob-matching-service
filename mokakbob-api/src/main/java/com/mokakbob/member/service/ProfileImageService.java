package com.mokakbob.member.service;

import com.mokakbob.member.domain.ProfileImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private static final String DEFAULT_PROFILE_DIRECTORY = "profile";

    private final ProfileImageUploader uploader;

    public String uploadProfileImage(MultipartFile file) {
        return uploader.upload(file, DEFAULT_PROFILE_DIRECTORY);
    }

    public void deleteProfileImage(String filePath) {
        uploader.delete(filePath);
    }
}
