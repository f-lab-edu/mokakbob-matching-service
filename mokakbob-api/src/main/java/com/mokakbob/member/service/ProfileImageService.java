package com.mokakbob.member.service;

import com.mokakbob.common.exception.ApiException;
import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.service.MemberService;
import com.mokakbob.member.domain.ProfileImageUploader;
import com.mokakbob.member.exception.MemberApiErrorCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private static final String DEFAULT_PROFILE_PATH = "profile/user-";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    private final ProfileImageUploader uploader;
    private final MemberService memberService;

    @Value("${profile.default.image.url}")
    private String defaultProfileImageUrl;

    @Transactional
    public String uploadProfileImage(MultipartFile file, Long memberId) {
        validateFile(file);

        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String fileKey = DEFAULT_PROFILE_PATH + memberId + "." + extension;

        Member member = memberService.findMember(memberId);
        deleteProfileImage(member.getProfileImage());
        member.updateProfileImage(fileKey);

        return uploader.upload(file, fileKey);
    }

    @Transactional
    public String applyDefaultImage(Long memberId) {
        Member member = memberService.findMember(memberId);

        String previousImagePath = member.getProfileImage();
        if (previousImagePath != null && !previousImagePath.isBlank() &&
                !previousImagePath.equals(defaultProfileImageUrl)) {
            uploader.delete(previousImagePath);
        }

        member.updateProfileImage(defaultProfileImageUrl);

        return defaultProfileImageUrl;
    }

    private void deleteProfileImage(String previousImagePath) {
        if (previousImagePath != null && !previousImagePath.isBlank()) {
            uploader.delete(previousImagePath);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(MemberApiErrorCode.IMAGE_EMPTY);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ApiException(MemberApiErrorCode.WRONG_IMAGE_NAME);
        }

        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ApiException(MemberApiErrorCode.WRONG_IMAGE_EXTENSION);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new ApiException(MemberApiErrorCode.WRONG_IMAGE_MIME);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(MemberApiErrorCode.IMAGE_VOLUME);
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
