package com.mokakbob.member.infrastructure;

import com.mokakbob.common.exception.ApiException;
import com.mokakbob.member.domain.ProfileImageUploader;
import com.mokakbob.member.exception.MemberApiErrorCode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
public class LocalProfileImageUploader implements ProfileImageUploader {

    private static final String PATH_SEPARATOR = "/";

    @Value("${local.upload.profile.directory}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file, String fileKey) {
        try {
            String fullPath = Paths.get(uploadDir, fileKey).toString();
            File folder = new File(Paths.get(uploadDir, getDirectory(fileKey)).toString());

            validateFolderExist(folder);

            file.transferTo(new File(fullPath));
            return fileKey.replace("\\", PATH_SEPARATOR);
        } catch (IOException e) {
            throw new ApiException(MemberApiErrorCode.IMAGE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public void delete(String filePath) {
        File file = new File(Paths.get(uploadDir, filePath).toString());

        if (file.exists()) {
            boolean success = file.delete();

            if (!success) {
                throw new ApiException(MemberApiErrorCode.IMAGE_DELETE_FAILED);
            }
        }
    }

    private void validateFolderExist(File folder) {
        if (!folder.exists()) {
            boolean success = folder.mkdirs();

            if (!success) {
                throw new ApiException(MemberApiErrorCode.IMAGE_FOLDER_CREATE_FAILED);
            }
        }
    }

    private String getDirectory(String fileKey) {
        int index = fileKey.lastIndexOf(PATH_SEPARATOR);

        if (index != -1) {
            return fileKey.substring(0, index);
        }

        return "";
    }

}
