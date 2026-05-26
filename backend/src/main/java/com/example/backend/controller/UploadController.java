package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.UploadResponse;
import com.example.backend.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/video")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadVideo(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(uploadService.uploadVideo(file)));
    }

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(uploadService.uploadImage(file)));
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(uploadService.uploadAvatar(file)));
    }
}
