package com.take.take_breath.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // 포인트 히스토리 조회
    Page<PointHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 특정 결제의 사용 금액 합계
    @Query("SELECT COALESCE(SUM(ph.amount), 0) FROM PointHistory ph " +
            "WHERE ph.payment.id = :paymentId AND ph.type = 'USE'")
    Long calculateUsedAmountByPaymentId(@Param("paymentId") Long paymentId);

    // 회원의 모든 충전 내역 조회 (오래된 순 - FIFO)
    @Query("SELECT ph FROM PointHistory ph " +
            "WHERE ph.member.id = :memberId AND ph.type = 'CHARGE' " +
            "ORDER BY ph.createdAt ASC")
    List<PointHistory> findChargeHistoriesForFifo(@Param("memberId") Long memberId);

    // 회원의 모든 사용 내역 조회 (오래된 순 - FIFO)
    @Query("SELECT ph FROM PointHistory ph " +
            "WHERE ph.member.id = :memberId AND ph.type = 'USE' " +
            "ORDER BY ph.createdAt ASC")
    List<PointHistory> findUseHistoriesForFifo(@Param("memberId") Long memberId);
}