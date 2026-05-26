package com.example.backend.service;

import com.example.backend.dto.request.MessageCreateRequest;
import com.example.backend.dto.response.MessageResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.DirectMessage;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.DirectMessageRepository;
import com.example.backend.repository.FollowRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {

    @Autowired
    private DirectMessageRepository messageRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MessageResponse sendMessage(MessageCreateRequest request, Long senderId) {
        Long receiverId = request.getReceiverId();
        if (receiverId.equals(senderId)) {
            throw new BusinessException("不能给自己发私聊");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", receiverId));
        if (receiver.getStatus() != User.Status.ACTIVE) {
            throw new BusinessException("该用户已被禁用");
        }

        if (!followRepository.existsByFollowerIdAndFollowingId(senderId, receiverId)) {
            throw new BusinessException("需先关注对方才能私聊");
        }

        DirectMessage message = new DirectMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(request.getContent());
        messageRepository.save(message);
        return toMessageResponse(message);
    }

    public PageResponse<MessageResponse> getConversation(Long userId, Long targetId, int page, int size) {
        userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", targetId));

        boolean hasRelation = followRepository.existsByFollowerIdAndFollowingId(userId, targetId)
                || followRepository.existsByFollowerIdAndFollowingId(targetId, userId);
        if (!hasRelation) {
            throw new BusinessException("需关注关系才能私聊");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DirectMessage> messagePage = messageRepository.findConversation(userId, targetId, pageable);
        return PageResponse.of(messagePage, this::toMessageResponse);
    }

    private MessageResponse toMessageResponse(DirectMessage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setContent(message.getContent());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
