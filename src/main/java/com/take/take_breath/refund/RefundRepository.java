package com.take.take_breath.refund;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    // 내 환불 내역 조회
    Page<Refund> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    
    // 특정 결제의 환불 내역 존재 여부
    boolean existsByPaymentId(Long paymentId);

    // 특정 결제의 환불 내역 조회
    Optional<Refund> findByPaymentId(Long paymentId);
}