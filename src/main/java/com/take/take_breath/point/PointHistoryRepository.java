package com.take.take_breath.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    
    // 포인트 히스토리 조회
    Page<PointHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}