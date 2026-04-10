package com.mokakbob.member.infrastructure;

import com.mokakbob.common.exception.ApiException;
import com.mokakbob.member.domain.ProfileImageUploader;
import com.mokakbob.member.exception.MemberApiErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
@RequiredArgsConstructor
public class S3ProfileImageUploader implements ProfileImageUploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String fileKey) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileKey;
        } catch (IOException e) {
            throw new ApiException(MemberApiErrorCode.IMAGE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public void delete(String filePath) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
