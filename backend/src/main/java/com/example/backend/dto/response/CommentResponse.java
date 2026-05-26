package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long videoId;
    private Long userId;
    private Long parentId;
    private Long rootId;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserResponse author;
    private Boolean isLiked;
    private List<CommentResponse> replies;
}
