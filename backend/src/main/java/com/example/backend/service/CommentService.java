package com.example.backend.service;

import com.example.backend.dto.request.CommentCreateRequest;
import com.example.backend.dto.response.CommentResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.Comment;
import com.example.backend.entity.LikeRecord;
import com.example.backend.entity.User;
import com.example.backend.entity.Video;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.LikeRecordRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRecordRepository likeRecordRepository;

    public PageResponse<CommentResponse> getComments(Long videoId, int page, int size, Long currentUserId) {
        videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", videoId));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByVideoIdAndStatusAndParentIdIsNull(
                videoId, Comment.Status.NORMAL, pageable);

        return toPageResponse(commentPage, currentUserId);
    }

    public PageResponse<CommentResponse> getReplies(Long parentId, int page, int size, Long currentUserId) {
        commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("评论", "id", parentId));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Comment> replyPage = commentRepository.findByParentIdAndStatus(parentId, Comment.Status.NORMAL, pageable);

        return toPageResponse(replyPage, currentUserId);
    }

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long userId) {
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", request.getVideoId()));

        Long parentId = request.getParentId();
        Long rootId = null;

        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("评论", "id", parentId));

            if (!parentComment.getVideoId().equals(request.getVideoId())) {
                throw new BusinessException("评论与视频不匹配");
            }

            if (parentComment.getRootId() != null) {
                rootId = parentComment.getRootId();
            } else {
                rootId = parentId;
            }

            commentRepository.incrementReplyCount(parentId);
        }

        Comment comment = new Comment();
        comment.setVideoId(request.getVideoId());
        comment.setUserId(userId);
        comment.setParentId(parentId);
        comment.setRootId(rootId);
        comment.setContent(request.getContent());
        comment.setStatus(Comment.Status.NORMAL);

        commentRepository.save(comment);

        video.setCommentCount(video.getCommentCount() + 1);
        videoRepository.save(video);

        return toCommentResponse(comment, userId);
    }

    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("评论", "id", id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));

        if (!comment.getUserId().equals(userId) && user.getRole() != User.Role.ADMIN) {
            throw new BusinessException("无权限删除此评论");
        }

        comment.setStatus(Comment.Status.DELETED);
        commentRepository.save(comment);

        Video video = videoRepository.findById(comment.getVideoId()).orElse(null);
        if (video != null) {
            video.setCommentCount(Math.max(0, video.getCommentCount() - 1));
            videoRepository.save(video);
        }
    }

    @Transactional
    public void likeComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("评论", "id", id));

        if (likeRecordRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, LikeRecord.TargetType.COMMENT, id)) {
            throw new BusinessException("已点赞");
        }

        LikeRecord like = new LikeRecord();
        like.setUserId(userId);
        like.setTargetType(LikeRecord.TargetType.COMMENT);
        like.setTargetId(id);
        likeRecordRepository.save(like);

        commentRepository.incrementLikeCount(id);
    }

    @Transactional
    public void unlikeComment(Long id, Long userId) {
        likeRecordRepository.deleteByUserIdAndTargetTypeAndTargetId(
                userId, LikeRecord.TargetType.COMMENT, id);

        commentRepository.decrementLikeCount(id);
    }

    private PageResponse<CommentResponse> toPageResponse(Page<Comment> commentPage, Long currentUserId) {
        return PageResponse.of(commentPage, c -> toCommentResponse(c, currentUserId));
    }

    private CommentResponse toCommentResponse(Comment comment, Long currentUserId) {
        User author = userRepository.findById(comment.getUserId()).orElse(null);

        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = likeRecordRepository.existsByUserIdAndTargetTypeAndTargetId(
                    currentUserId, LikeRecord.TargetType.COMMENT, comment.getId());
        }

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setVideoId(comment.getVideoId());
        response.setUserId(comment.getUserId());
        response.setParentId(comment.getParentId());
        response.setRootId(comment.getRootId());
        response.setContent(comment.getContent());
        response.setLikeCount(comment.getLikeCount());
        response.setReplyCount(comment.getReplyCount());
        response.setStatus(comment.getStatus().name());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        response.setAuthor(author != null ? toUserResponse(author) : null);
        response.setIsLiked(isLiked);
        return response;
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setRole(user.getRole().name());
        return response;
    }
}
