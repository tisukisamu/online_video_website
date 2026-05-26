package com.example.backend.repository;

import com.example.backend.entity.LikeRecord;
import com.example.backend.entity.LikeRecord.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRecordRepository extends JpaRepository<LikeRecord, Long> {

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    Optional<LikeRecord> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    long countByTargetTypeAndTargetId(TargetType targetType, Long targetId);

    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
}
