package com.take.take_breath.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentRequest {

    // 결제 준비 요청
    @Data
    @NoArgsConstructor
    public static class PrepareDTO {
        private Long amount;         // 결제 금액
        private String orderName;    // 주문명
        private String buyerName;    // 구매자명
        private String buyerEmail;   // 구매자 이메일
        private String buyerTel;     // 구매자 전화번호
    }

    // 결제 검증 요청
    @Data
    @NoArgsConstructor
    public static class VerifyDTO {
        private String impUid;       // 포트원 결제 고유번호
        private String merchantUid;  // 가맹점 주문번호
    }
}
