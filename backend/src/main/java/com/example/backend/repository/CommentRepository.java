package com.example.backend.repository;

import com.example.backend.entity.Comment;
import com.example.backend.entity.Comment.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByVideoIdAndStatus(Long videoId, Status status, Pageable pageable);

    Page<Comment> findByVideoIdAndStatusAndParentIdIsNull(Long videoId, Status status, Pageable pageable);

    Page<Comment> findByParentIdAndStatus(Long parentId, Status status, Pageable pageable);

    List<Comment> findByRootIdAndStatus(Long rootId, Status status);

    Page<Comment> findByUserId(Long userId, Pageable pageable);

    long countByVideoIdAndStatus(Long videoId, Status status);

    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = CASE WHEN c.likeCount > 0 THEN c.likeCount - 1 ELSE 0 END WHERE c.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Comment c SET c.replyCount = c.replyCount + 1 WHERE c.id = :id")
    void incrementReplyCount(@Param("id") Long id);
}
