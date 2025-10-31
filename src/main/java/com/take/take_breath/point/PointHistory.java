package com.take.take_breath.point;

import com.take.take_breath.members.Member;
import com.take.take_breath.payment.Payment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "point_history_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 포인트 변동 회원 (USER 또는 COUNSELOR)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType type;  // 거래 타입
    
    @Column(nullable = false)
    private Long amount;  // 변동 금액
    
    @Column(nullable = false)
    private Long balanceAfter;  // 거래 후 잔액
    
    private String description;  // 설명
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_member_id")
    private Member relatedMember;  // 연관된 상대방 (USER 또는 COUNSELOR)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;  // 어느 충전건에서 사용/환불되었는지 추적 (CHARGE, USE, REFUND에서 사용)

    @CreationTimestamp
    private Timestamp createdAt;  // 거래 시간
}