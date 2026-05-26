package com.example.backend.repository;

import com.example.backend.entity.Video;
import com.example.backend.entity.Video.Status;
import com.example.backend.entity.Video.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findByStatus(Status status, Pageable pageable);

    Page<Video> findByStatusAndVisibility(Status status, Visibility visibility, Pageable pageable);

    Page<Video> findByUserId(Long userId, Pageable pageable);

    Page<Video> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);

    @Query("SELECT v FROM Video v WHERE v.status = :status AND (v.title LIKE %:keyword% OR v.description LIKE %:keyword%)")
    Page<Video> searchByKeyword(@Param("status") Status status, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT v FROM Video v WHERE v.userId IN :userIds AND v.status = :status ORDER BY v.createdAt DESC")
    Page<Video> findByUserIdsAndStatus(@Param("userIds") List<Long> userIds, @Param("status") Status status, Pageable pageable);

    long countByUserId(Long userId);
}
