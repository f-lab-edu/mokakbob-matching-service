package com.mokakbob.member.infrastructure;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.member.domain.ProfileImageUploader;
import com.mokakbob.member.exception.MemberApiErrorCode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalProfileImageUploader implements ProfileImageUploader {

    private static final String FILE_NAME_CONNECTING = "_";

    @Value("${local.upload.profile.directory}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file, String directory) {
        try {
            String filename = UUID.randomUUID() + FILE_NAME_CONNECTING + file.getOriginalFilename();
            String folderPath = Paths.get(uploadDir, directory)
                    .toString();
            File folder = new File(folderPath);

            validateFolderExist(folder);

            String filePath = Paths.get(folderPath, filename)
                    .toString();
            file.transferTo(new File(filePath));

            return Paths.get(directory, filename)
                    .toString()
                    .replace("\\", "/");
        } catch (IOException e) {
            throw new ApiException(MemberApiErrorCode.IMAGE_UPLOAD_FAILED);
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
}
