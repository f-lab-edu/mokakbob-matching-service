package com.mokakbob.member.config;

import com.mokakbob.member.infrastructure.LocalProfileImageUploader;
import com.mokakbob.member.infrastructure.S3ProfileImageUploader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ProfileImageUploaderConfig {

    @Bean
    @Profile({"local", "dev", "test"})
    public LocalProfileImageUploader localProfileImageUploader() {
        return new LocalProfileImageUploader();
    }

    @Bean
    @Profile("prod")
    public S3ProfileImageUploader s3ProfileImageUploader(S3Client s3Client) {
        return new S3ProfileImageUploader(s3Client);
    }
}
