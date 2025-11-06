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

    Optional<Payment> findByImpUid(String impUid);

    Page<Payment> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    /**
     * FIFO: 가장 오래된 결제건부터 조회
     * (포인트 사용 시 오래된 충전건부터 차감하기 위함)
     */
    @Query("SELECT p FROM Payment p " + "WHERE p.member.id = :memberId " +
            "AND p.status = :status " + "ORDER BY p.paidAt ASC")
    List<Payment> findAvailablePayments(@Param("memberId") Long memberId, @Param("status") PaymentStatus status);

    // 수수료 관련 통계
    @Query("SELECT SUM(p.feeAmount) FROM Payment p WHERE p.status = 'PAID'")
    Long getTotalFeeAmount();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID'")
    Long countPaidPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID'")
    Long getTotalPaymentAmount();

    @Query(value = """
    SELECT 
        DATE_FORMAT(p.created_at, '%Y-%m') AS month,
        SUM(p.fee_amount) AS total_fee,
        SUM(p.amount) AS total_amount,
        COUNT(p.id) AS payment_count
    FROM payment_tb p
    WHERE p.status = 'PAID'
    GROUP BY DATE_FORMAT(p.created_at, '%Y-%m')
    ORDER BY month DESC
""", nativeQuery = true)
    List<Object[]> getMonthlyFeeStats();
}