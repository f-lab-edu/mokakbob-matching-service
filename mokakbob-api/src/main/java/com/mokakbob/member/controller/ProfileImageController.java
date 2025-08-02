package com.mokakbob.member.controller;

import com.mokakbob.global.resolver.annotation.MemberId;
import com.mokakbob.member.controller.response.ImageResponse;
import com.mokakbob.member.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping("/image/upload")
    public ResponseEntity<ImageResponse> uploadImage(
            @RequestPart("file") MultipartFile file,
            @MemberId Long memberId
    ) {
        String imagePath = profileImageService.uploadProfileImage(file, memberId);
        return ResponseEntity.ok(new ImageResponse(imagePath));
    }

    @PostMapping("/image/default")
    public ResponseEntity<ImageResponse> defaultImage(@MemberId Long memberId) {
        String imagePath = profileImageService.applyDefaultImage(memberId);
        return ResponseEntity.ok(new ImageResponse(imagePath));
    }
}
