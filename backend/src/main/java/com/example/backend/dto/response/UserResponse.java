package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String avatar;
    private String bio;
    private String role;
    private String status;
    private LocalDateTime createdAt;

    private Long videoCount;
    private Long followerCount;
    private Long followingCount;
    private Long likeCount;
    private Boolean isFollowing;
}
