package com.example.backend.repository;

import com.example.backend.entity.DirectMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("SELECT m FROM DirectMessage m WHERE (m.senderId = :userId AND m.receiverId = :targetId) OR (m.senderId = :targetId AND m.receiverId = :userId)")
    Page<DirectMessage> findConversation(@Param("userId") Long userId,
                                         @Param("targetId") Long targetId,
                                         Pageable pageable);
}
