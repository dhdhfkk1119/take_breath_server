package com.take.take_breath.refund;

import com.take.take_breath.members.Member;
import com.take.take_breath.payment.Payment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "refund_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(nullable = false)
    private Long refundAmount;  // 환불 금액
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;
    
    private String reason;  // 환불 사유
    
    private String impCancelUid;  // 포트원 취소 고유번호
    
    @CreationTimestamp
    private Timestamp createdAt;
    
    private Timestamp processedAt;  // 처리 시간
    
    // 환불 완료 처리
    public void completeRefund(String impCancelUid) {
        this.status = RefundStatus.COMPLETED;
        this.impCancelUid = impCancelUid;
        this.processedAt = new Timestamp(System.currentTimeMillis());
    }
}