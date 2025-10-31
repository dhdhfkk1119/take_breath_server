package com.take.take_breath.payment;

import com.take.take_breath.members.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "payment_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = true, unique = true)
    private String impUid;          // 포트원 결제 고유번호 (포트원에서 생성)

    @Column(nullable = false, unique = true)
    private String merchantUid;     // 가맹점 주문번호 (우리가 생성)

    @Column(nullable = false)
    private Long pointAmount;    // 실제 적립되는 포인트 금액

    @Column(nullable = false)
    private Long feeAmount;      // 수수료 금액

    @Column(nullable = false)
    private Double feeRate;     // 수수료 비율 (0.1 = 10%)

    @Column(nullable = false)
    private Long amount;    // 총 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;  // 결제 상태 (PENDING, PAID, FAILED, CANCELLED)

    private String payMethod;  // 결제 수단

    private String orderName;  // 주문명

    private String buyerName;  // 구매자명

    private String buyerEmail;  // 구매자 이메일

    private String buyerTel;  // 구매자 전화번호

    @CreationTimestamp
    private Timestamp createdAt;  // 결제 생성 시간

    private Timestamp paidAt;  // 결제 완료 시간

    // 결제 완료 처리
    public void completePay(String impUid, String payMethod) {
        this.impUid = impUid;
        this.status = PaymentStatus.PAID;
        this.payMethod = payMethod;
        this.paidAt = new Timestamp(System.currentTimeMillis());
    }
}
