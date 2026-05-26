package com.example.backend.service;

import com.example.backend.dto.response.UploadResponse;
import com.example.backend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Value("${upload.video.max-size:524288000}")
    private long maxVideoSize;

    @Value("${upload.image.max-size:10485760}")
    private long maxImageSize;
    
    // 获取绝对路径的辅助方法
    private Path getUploadPath() {
        return Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/webm"
    );

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public UploadResponse uploadVideo(MultipartFile file) {
        validateFile(file, ALLOWED_VIDEO_TYPES, maxVideoSize);

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String fileName = generateFileName(file.getOriginalFilename());
        String relativePath = "videos/" + datePath + "/" + fileName;

        return saveFile(file, relativePath, fileName);
    }

    public UploadResponse uploadImage(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES, maxImageSize);

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String fileName = generateFileName(file.getOriginalFilename());
        String relativePath = "images/" + datePath + "/" + fileName;

        return saveFile(file, relativePath, fileName);
    }

    public UploadResponse uploadAvatar(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES, maxImageSize);

        String fileName = generateFileName(file.getOriginalFilename());
        String relativePath = "avatars/" + fileName;

        return saveFile(file, relativePath, fileName);
    }

    private UploadResponse saveFile(MultipartFile file, String relativePath, String fileName) {
        Path targetPath = getUploadPath().resolve(relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        // 修改为 /api/uploads/ 开头，以便通过 ResourceController 访问
        String fileUrl = "/api/uploads/" + relativePath.replace("\\", "/");

        UploadResponse response = new UploadResponse();
        response.setUrl(fileUrl);
        response.setFileName(fileName);
        response.setFileSize(file.getSize());
        return response;
    }

    private void validateFile(MultipartFile file, List<String> allowedTypes, long maxSize) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BusinessException("不支持的文件类型: " + contentType);
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超出限制，最大允许 " + (maxSize / 1024 / 1024) + "MB");
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    public void deleteFile(String relativePath) {
        Path filePath = Paths.get(uploadPath, relativePath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }
}
