package com.take.take_breath.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMerchantUid(String merchantUid);
    Optional<Payment> findByImpUid(String impUid);

    Page<Payment> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}