package com.example.yourproject.controller;

import com.example.yourproject.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.yourproject.exception.FileStorageException;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "请选择要上传的图片文件"));
        }

        try {
            String imageUrl = fileStorageService.store(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "图片上传处理失败: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "图片上传参数无效: " + e.getMessage()));
        }
    }
}