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
public class VideoResponse {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String coverUrl;
    private String videoUrl;
    private Integer duration;
    private Long fileSize;
    private String status;
    private String visibility;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;
    private Long favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserResponse author;
    private Boolean isLiked;
    private Boolean isFavorited;
}
