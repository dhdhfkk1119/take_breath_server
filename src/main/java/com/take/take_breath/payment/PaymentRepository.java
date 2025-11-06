package com.take.take_breath.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMerchantUid(String merchantUid);

    /**
     * 결제 내역 조회 (PAID, CANCELLED만)
     */
    @Query("SELECT p FROM Payment p " +
            "WHERE p.member.id = :memberId " +
            "AND (p.status = 'PAID' OR p.status = 'CANCELLED') " +
            "ORDER BY p.createdAt DESC")
    Page<Payment> findPaymentHistoryByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 수수료 관련 통계
    @Query("SELECT SUM(p.feeAmount) FROM Payment p WHERE p.status = 'PAID'")
    Long getTotalFeeAmount();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID'")
    Long countPaidPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID'")
    Long getTotalPaymentAmount();

    @Query(value = """
    SELECT 
        DATE_FORMAT(p.paid_at, '%Y-%m') AS month,
        SUM(p.fee_amount) AS totalFeeAmount,
        COUNT(*) AS totalPaymentCount,
        SUM(p.amount) AS totalPaymentAmount
    FROM payment_tb p
    WHERE p.status = 'PAID'
      AND p.paid_at IS NOT NULL
    GROUP BY DATE_FORMAT(p.paid_at, '%Y-%m')
    ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyFeeStats();
}