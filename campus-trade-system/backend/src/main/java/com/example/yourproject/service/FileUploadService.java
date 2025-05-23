package com.example.yourproject.service;

import com.example.yourproject.config.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Objects;

@Service
public class FileUploadService {

    private final Path fileStorageLocation;

    @Autowired
    public FileUploadService(StorageProperties storageProperties) {
        this.fileStorageLocation = Paths.get(storageProperties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建文件存储目录。", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Check if the file's name contains invalid characters
        if(originalFilename.contains("..")) {
            throw new IllegalArgumentException("文件名包含无效路径序列 " + originalFilename);
        }

        // Validate file type (simple check by extension)
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i+1).toLowerCase();
        }
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("gif")) {
            throw new IllegalArgumentException("文件类型不支持，请上传 png, jpg, jpeg, gif 格式的图片。");
        }
        
        // Validate file size (e.g., max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制 (" + maxSize / (1024 * 1024) + "MB)");
        }


        // Create a unique filename
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        // Return the relative path to be used in URL
        // This path should correspond to how static resources are served
        return "/uploads/images/" + uniqueFileName; // Assuming storage.location resolves to a parent of 'images' if configured like './uploads' and you want /uploads/images/
                                                   // Or if storage.location is './uploads/images', then just "/images/" + uniqueFileName or even "/uploads/images/"
                                                   // This needs to align with your static resource handler
    }
}