package com.mokakbob.member.infrastructure;

import com.mokakbob.member.domain.ProfileImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@Profile("s3")
@RequiredArgsConstructor
public class S3ProfileImageUploader implements ProfileImageUploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String directory) {
        return "";
    }

    @Override
    public void delete(String filePath) {

    }
}
