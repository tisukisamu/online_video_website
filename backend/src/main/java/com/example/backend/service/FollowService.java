package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.Follow;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FollowRepository;
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
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    public void follow(Long followingId, Long followerId) {
        if (followingId.equals(followerId)) {
            throw new BusinessException("不能关注自己");
        }

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", followingId));

        if (following.getStatus() != User.Status.ACTIVE) {
            throw new BusinessException("该用户已被禁用");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException("已关注该用户");
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followingId, Long followerId) {
        followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(followRepository::delete);
    }

    public boolean isFollowing(Long followingId, Long followerId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public PageResponse<UserResponse> getFollowing(Long userId, int page, int size) {
        return getFollowing(userId, page, size, null);
    }

    public PageResponse<UserResponse> getFollowing(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Follow> followPage = followRepository.findByFollowerId(userId, pageable);

        List<UserResponse> list = followPage.getContent().stream()
                .map(follow -> userRepository.findById(follow.getFollowingId()))
                .filter(opt -> opt.isPresent() && opt.get().getStatus() == User.Status.ACTIVE)
                .map(opt -> toUserResponse(opt.get(), currentUserId))
                .collect(Collectors.toList());

        return PageResponse.of(list,
                followPage.getNumber() + 1,
                followPage.getSize(),
                followPage.getTotalElements(),
                followPage.getTotalPages());
    }

    public PageResponse<UserResponse> getFollowers(Long userId, int page, int size) {
        return getFollowers(userId, page, size, null);
    }

    public PageResponse<UserResponse> getFollowers(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Follow> followPage = followRepository.findByFollowingId(userId, pageable);

        List<UserResponse> list = followPage.getContent().stream()
                .map(follow -> userRepository.findById(follow.getFollowerId()))
                .filter(opt -> opt.isPresent() && opt.get().getStatus() == User.Status.ACTIVE)
                .map(opt -> toUserResponse(opt.get(), currentUserId))
                .collect(Collectors.toList());

        return PageResponse.of(list,
                followPage.getNumber() + 1,
                followPage.getSize(),
                followPage.getTotalElements(),
                followPage.getTotalPages());
    }

    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
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
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setCreatedAt(user.getCreatedAt());
        response.setVideoCount(videoCount);
        response.setFollowerCount(followerCount);
        response.setFollowingCount(followingCount);
        response.setIsFollowing(isFollowing);
        return response;
    }
}
