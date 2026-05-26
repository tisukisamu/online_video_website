package com.example.backend.service;

import com.example.backend.dto.request.VideoCreateRequest;
import com.example.backend.dto.request.VideoUpdateRequest;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.dto.response.VideoResponse;
import com.example.backend.entity.LikeRecord;
import com.example.backend.entity.User;
import com.example.backend.entity.Video;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FavoriteRepository;
import com.example.backend.repository.FollowRepository;
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

import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRecordRepository likeRecordRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FollowRepository followRepository;

    public PageResponse<VideoResponse> getVideoList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videoPage = videoRepository.findByStatus(Video.Status.PUBLISHED, pageable);
        return PageResponse.of(videoPage, v -> toVideoResponse(v, null));
    }

    public PageResponse<VideoResponse> getRecommendVideos(int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "viewCount")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<Video> videoPage = videoRepository.findByStatusAndVisibility(
                Video.Status.PUBLISHED, Video.Visibility.PUBLIC, pageable);
        return PageResponse.of(videoPage, v -> toVideoResponse(v, currentUserId));
    }

    public PageResponse<VideoResponse> getFollowVideos(int page, int size, Long currentUserId) {
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(currentUserId);
        if (followingIds.isEmpty()) {
            return PageResponse.of(List.of(), page, size, 0, 0);
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videoPage = videoRepository.findByUserIdsAndStatus(followingIds, Video.Status.PUBLISHED, pageable);
        return PageResponse.of(videoPage, v -> toVideoResponse(v, currentUserId));
    }

    public PageResponse<VideoResponse> searchVideos(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videoPage = videoRepository.searchByKeyword(Video.Status.PUBLISHED, keyword, pageable);
        return PageResponse.of(videoPage, v -> toVideoResponse(v, null));
    }

    public VideoResponse getVideoDetail(Long id, Long currentUserId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", id));

        if (video.getStatus() == Video.Status.DELETED) {
            throw new BusinessException("视频已被删除");
        }

        if (video.getVisibility() == Video.Visibility.PRIVATE) {
            if (currentUserId == null || !currentUserId.equals(video.getUserId())) {
                throw new BusinessException("无权查看此视频");
            }
        }

        if (video.getVisibility() == Video.Visibility.FOLLOWERS) {
            if (currentUserId == null || !currentUserId.equals(video.getUserId())) {
                boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, video.getUserId());
                if (!isFollowing) {
                    throw new BusinessException("仅粉丝可见");
                }
            }
        }

        return toVideoResponse(video, currentUserId);
    }

    @Transactional
    public VideoResponse createVideo(VideoCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));

        Video video = new Video();
        video.setUserId(userId);
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setCoverUrl(request.getCoverUrl());
        video.setVideoUrl(request.getVideoUrl());
        video.setDuration(request.getDuration());
        video.setFileSize(request.getFileSize() != null ? request.getFileSize() : 0L);
        video.setVisibility(request.getVisibility() != null ? 
                Video.Visibility.valueOf(request.getVisibility()) : Video.Visibility.PUBLIC);
        
        // Admin videos are published immediately; others need audit
        if (user.getRole() == User.Role.ADMIN) {
            video.setStatus(Video.Status.PUBLISHED);
        } else {
            video.setStatus(Video.Status.PENDING);
        }

        videoRepository.save(video);
        return toVideoResponse(video, userId);
    }

    @Transactional
    public VideoResponse auditVideo(Long videoId, Video.Status status, String reason) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", videoId));

        if (status == Video.Status.PUBLISHED || status == Video.Status.REJECTED || status == Video.Status.BANNED) {
            video.setStatus(status);
            // In a real app, you might save the rejection reason or notify the user
        } else {
            throw new BusinessException("无效的审核状态");
        }
        
        return toVideoResponse(videoRepository.save(video), null);
    }

    @Transactional
    public VideoResponse updateVideo(Long id, VideoUpdateRequest request, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", id));

        if (!video.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此视频");
        }

        if (request.getTitle() != null) {
            video.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            video.setDescription(request.getDescription());
        }
        if (request.getCoverUrl() != null) {
            video.setCoverUrl(request.getCoverUrl());
        }
        if (request.getVisibility() != null) {
            video.setVisibility(Video.Visibility.valueOf(request.getVisibility()));
        }

        videoRepository.save(video);
        return toVideoResponse(video, userId);
    }

    @Transactional
    public void deleteVideo(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));

        if (!video.getUserId().equals(userId) && user.getRole() != User.Role.ADMIN) {
            throw new BusinessException("无权限删除此视频");
        }

        video.setStatus(Video.Status.DELETED);
        videoRepository.save(video);
    }

    @Transactional
    public void recordView(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", id));
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);
    }

    @Transactional
    public void likeVideo(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", id));

        if (likeRecordRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, LikeRecord.TargetType.VIDEO, id)) {
            throw new BusinessException("已点赞");
        }

        LikeRecord like = new LikeRecord();
        like.setUserId(userId);
        like.setTargetType(LikeRecord.TargetType.VIDEO);
        like.setTargetId(id);
        likeRecordRepository.save(like);

        video.setLikeCount(video.getLikeCount() + 1);
        videoRepository.save(video);
    }

    @Transactional
    public void unlikeVideo(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", id));

        likeRecordRepository.deleteByUserIdAndTargetTypeAndTargetId(
                userId, LikeRecord.TargetType.VIDEO, id);

        video.setLikeCount(Math.max(0, video.getLikeCount() - 1));
        videoRepository.save(video);
    }

    public PageResponse<VideoResponse> getUserVideos(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Video> videoPage = videoRepository.findByUserIdAndStatus(userId, Video.Status.PUBLISHED, pageable);
        return PageResponse.of(videoPage, v -> toVideoResponse(v, null));
    }

    private VideoResponse toVideoResponse(Video video, Long currentUserId) {
        User author = userRepository.findById(video.getUserId()).orElse(null);

        boolean isLiked = false;
        boolean isFavorited = false;
        if (currentUserId != null) {
            isLiked = likeRecordRepository.existsByUserIdAndTargetTypeAndTargetId(
                    currentUserId, LikeRecord.TargetType.VIDEO, video.getId());
            isFavorited = favoriteRepository.existsByUserIdAndVideoId(currentUserId, video.getId());
        }

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
        response.setAuthor(author != null ? toUserResponse(author) : null);
        response.setIsLiked(isLiked);
        response.setIsFavorited(isFavorited);
        return response;
    }

    private UserResponse toUserResponse(User user) {
        long videoCount = videoRepository.countByUserId(user.getId());
        long followerCount = followRepository.countByFollowingId(user.getId());
        long followingCount = followRepository.countByFollowerId(user.getId());

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
}
