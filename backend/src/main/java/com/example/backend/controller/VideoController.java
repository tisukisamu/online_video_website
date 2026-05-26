package com.example.backend.controller;

import com.example.backend.dto.request.VideoCreateRequest;
import com.example.backend.dto.request.VideoUpdateRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.VideoResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> getVideoList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(videoService.getVideoList(page, size)));
    }

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> getRecommendVideos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(videoService.getRecommendVideos(page, size, userId)));
    }

    @GetMapping("/follow")
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> getFollowVideos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                videoService.getFollowVideos(page, size, userDetails.getId())));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> searchVideos(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(videoService.searchVideos(keyword, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoResponse>> getVideoDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(videoService.getVideoDetail(id, userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VideoResponse>> createVideo(
            @Valid @RequestBody VideoCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                videoService.createVideo(request, userDetails.getId())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoResponse>> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody VideoUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                videoService.updateVideo(id, request, userDetails.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        videoService.deleteVideo(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> recordView(@PathVariable Long id) {
        videoService.recordView(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> likeVideo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        videoService.likeVideo(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeVideo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        videoService.unlikeVideo(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("取消点赞", null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> getUserVideos(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(videoService.getUserVideos(userId, page, size)));
    }
}
