package com.example.backend.repository;

import com.example.backend.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndVideoId(Long userId, Long videoId);

    Optional<Favorite> findByUserIdAndVideoId(Long userId, Long videoId);

    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    @Query("SELECT f.videoId FROM Favorite f WHERE f.userId = :userId")
    List<Long> findVideoIdsByUserId(@Param("userId") Long userId);
}
