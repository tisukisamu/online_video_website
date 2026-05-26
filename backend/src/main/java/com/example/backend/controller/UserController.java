package com.example.backend.controller;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.dto.response.VideoResponse;
import com.example.backend.entity.User;
import com.example.backend.entity.Video;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FollowRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VideoRepository;
import com.example.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private FollowRepository followRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));

        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(toUserResponse(user, currentUserId)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
        return ResponseEntity.ok(ApiResponse.success(toUserResponse(user, userDetails.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(toUserResponse(user, userDetails.getId())));
    }

    @GetMapping("/me/videos")
    public ResponseEntity<ApiResponse<PageResponse<VideoResponse>>> getMyVideos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                getUserVideos(userDetails.getId(), true, page, size)));
    }

    private UserResponse toUserResponse(User user, Long currentUserId) {
        long videoCount = videoRepository.countByUserId(user.getId());
        long followerCount = followRepository.countByFollowingId(user.getId());
        long followingCount = followRepository.countByFollowerId(user.getId());

        boolean isFollowing = false;
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setCreatedAt(user.getCreatedAt());
        response.setVideoCount(videoCount);
        response.setFollowerCount(followerCount);
        response.setFollowingCount(followingCount);
        return response;
    }

    private PageResponse<VideoResponse> getUserVideos(Long userId, boolean isSelf, int page, int size) {
        org.springframework.data.domain.Page<Video> videoPage;
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        if (isSelf) {
            videoPage = videoRepository.findByUserId(userId, pageRequest);
        } else {
            videoPage = videoRepository.findByUserIdAndStatus(userId, Video.Status.PUBLISHED, pageRequest);
        }

        User user = userRepository.findById(userId).orElse(null);
        UserResponse authorResponse = user != null ? toUserResponse(user, null) : null;

        return PageResponse.of(
                videoPage,
                video -> {
                    VideoResponse response = new VideoResponse();
                    response.setId(video.getId());
                    response.setUserId(video.getUserId());
                    response.setTitle(video.getTitle());
                    response.setDescription(video.getDescription());
                    response.setCoverUrl(video.getCoverUrl());
                    response.setVideoUrl(video.getVideoUrl());
                    response.setDuration(video.getDuration());
                    response.setFileSize(video.getFileSize());
                    response.setStatus(video.getStatus().name());
                    response.setVisibility(video.getVisibility().name());
                    response.setViewCount(video.getViewCount());
                    response.setLikeCount(video.getLikeCount());
                    response.setCommentCount(video.getCommentCount());
                    response.setShareCount(video.getShareCount());
                    response.setFavoriteCount(video.getFavoriteCount());
                    response.setCreatedAt(video.getCreatedAt());
                    response.setUpdatedAt(video.getUpdatedAt());
                    response.setAuthor(authorResponse);
                    return response;
                }
        );
    }
}
