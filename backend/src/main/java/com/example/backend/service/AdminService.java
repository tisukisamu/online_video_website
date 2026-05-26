package com.example.backend.service;

import com.example.backend.dto.UserDTO;
import com.example.backend.dto.response.CommentResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.dto.response.VideoResponse;
import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import com.example.backend.entity.Video;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final com.example.backend.repository.VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final VideoService videoService;

    public PageResponse<VideoResponse> getVideoList(int page, int size, Video.Status status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videoPage;
        if (status != null) {
            videoPage = videoRepository.findByStatus(status, pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }
        // Use VideoService to get details (reusing existing logic)
        // Note: getVideoDetail throws if not published/owner, so we might need a raw mapper here.
        // Or we can just map basic fields.
        return PageResponse.of(videoPage, v -> {
            // Simplified mapping for admin list
            VideoResponse res = new VideoResponse();
            res.setId(v.getId());
            res.setTitle(v.getTitle());
            res.setCoverUrl(v.getCoverUrl());
            res.setStatus(v.getStatus().name());
            res.setCreatedAt(v.getCreatedAt());
            res.setUserId(v.getUserId());
            // Add other fields as needed
            return res;
        });
    }

    @Transactional
    public VideoResponse auditVideo(Long videoId, Video.Status status, String reason) {
        return videoService.auditVideo(videoId, status, reason);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUserStatus(Long id, User.Status status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        user.setStatus(status);
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        user.setRole(role);
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        userRepository.deleteById(id);
    }

    public Map<String, Object> getDashboardStats() {
        List<User> users = userRepository.findAll();
        
        long totalUsers = users.size();
        long activeUsers = users.stream().filter(u -> u.getStatus() == User.Status.ACTIVE).count();
        long disabledUsers = users.stream().filter(u -> u.getStatus() == User.Status.DISABLED).count();
        long adminCount = users.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        long userCount = users.stream().filter(u -> u.getRole() == User.Role.USER).count();

        long videoCount = videoRepository.count();
        long commentCount = commentRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("disabledUsers", disabledUsers);
        stats.put("adminCount", adminCount);
        stats.put("userCount", userCount);
        stats.put("videoCount", videoCount);
        stats.put("commentCount", commentCount);
        
        return stats;
    }

    public PageResponse<CommentResponse> getCommentList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findAll(pageable);
        
        return PageResponse.of(commentPage, comment -> {
            CommentResponse res = new CommentResponse();
            res.setId(comment.getId());
            res.setVideoId(comment.getVideoId());
            res.setUserId(comment.getUserId());
            res.setParentId(comment.getParentId());
            res.setRootId(comment.getRootId());
            res.setContent(comment.getContent());
            res.setLikeCount(comment.getLikeCount());
            res.setReplyCount(comment.getReplyCount());
            res.setStatus(comment.getStatus().name());
            res.setCreatedAt(comment.getCreatedAt());
            res.setUpdatedAt(comment.getUpdatedAt());
            
            userRepository.findById(comment.getUserId()).ifPresent(user -> {
                UserResponse author = new UserResponse();
                author.setId(user.getId());
                author.setName(user.getName());
                author.setUsername(user.getUsername());
                author.setAvatar(user.getAvatar());
                res.setAuthor(author);
            });
            
            return res;
        });
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("评论", "id", id));
        comment.setStatus(Comment.Status.DELETED);
        commentRepository.save(comment);
    }
}
