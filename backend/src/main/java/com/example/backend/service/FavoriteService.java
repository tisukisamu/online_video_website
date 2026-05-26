package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.dto.response.VideoResponse;
import com.example.backend.entity.Favorite;
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
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRecordRepository likeRecordRepository;

    @Autowired
    private FollowRepository followRepository;

    @Transactional
    public void addFavorite(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("视频", "id", videoId));

        if (video.getStatus() != Video.Status.PUBLISHED) {
            throw new BusinessException("视频不可收藏");
        }

        if (favoriteRepository.existsByUserIdAndVideoId(userId, videoId)) {
            throw new BusinessException("已收藏该视频");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setVideoId(videoId);
        favoriteRepository.save(favorite);

        video.setFavoriteCount(video.getFavoriteCount() + 1);
        videoRepository.save(video);
    }

    @Transactional
    public void removeFavorite(Long videoId, Long userId) {
        favoriteRepository.findByUserIdAndVideoId(userId, videoId)
                .ifPresent(favorite -> {
                    favoriteRepository.delete(favorite);
                    
                    Video video = videoRepository.findById(videoId).orElse(null);
                    if (video != null) {
                        video.setFavoriteCount(Math.max(0, video.getFavoriteCount() - 1));
                        videoRepository.save(video);
                    }
                });
    }

    public PageResponse<VideoResponse> getUserFavorites(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Favorite> favoritePage = favoriteRepository.findByUserId(userId, pageable);

        List<VideoResponse> list = favoritePage.getContent().stream()
                .map(favorite -> videoRepository.findById(favorite.getVideoId()))
                .filter(opt -> opt.isPresent() && opt.get().getStatus() == Video.Status.PUBLISHED)
                .map(opt -> toVideoResponse(opt.get(), userId))
                .collect(Collectors.toList());

        return PageResponse.of(list,
                favoritePage.getNumber() + 1,
                favoritePage.getSize(),
                favoritePage.getTotalElements(),
                favoritePage.getTotalPages());
    }

    public boolean isFavorited(Long videoId, Long userId) {
        return favoriteRepository.existsByUserIdAndVideoId(userId, videoId);
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
        response.setAvatar(user.getEmail());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setCreatedAt(user.getCreatedAt());
        response.setVideoCount(videoCount);
        response.setFollowerCount(followerCount);
        response.setFollowingCount(followingCount);
        return response;
    }
}
