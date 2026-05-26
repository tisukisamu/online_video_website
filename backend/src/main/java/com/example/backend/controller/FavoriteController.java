package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.dto.response.VideoResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> getUserFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                favoriteService.getUserFavorites(userDetails.getId(), page, size)));
    }

    @PostMapping("/{videoId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable Long videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        favoriteService.addFavorite(videoId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("收藏成功", null));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        favoriteService.removeFavorite(videoId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("取消收藏", null));
    }

    @GetMapping("/{videoId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isFavorited(
            @PathVariable Long videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isFavorited = favoriteService.isFavorited(videoId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(isFavorited));
    }
}
