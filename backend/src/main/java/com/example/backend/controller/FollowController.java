package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.follow(userId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("关注成功", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.unfollow(userId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("取消关注", null));
    }

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getFollowing(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowing(userDetails.getId(), page, size)));
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getFollowers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowers(userDetails.getId(), page, size)));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowing(userId, page, size, currentUserId)));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowers(userId, page, size, currentUserId)));
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isFollowing = followService.isFollowing(userId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }
}
