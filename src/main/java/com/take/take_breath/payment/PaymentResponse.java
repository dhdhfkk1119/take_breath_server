package com.take.take_breath.payment;

import com.take.take_breath._core._utils.DateUtil;
import lombok.Builder;
import lombok.Data;

public class PaymentResponse {

    // 결제 준비 응답
    @Data
    @Builder
    public static class PrepareDTO {
        private String merchantUid;
        private Long amount;
        private String orderName;
        private String buyerName;
        private String buyerEmail;
        private String buyerTel;
    }

    // 결제 목록 조회
    @Data
    public static class ListDTO {
        private Long id;
        private String merchantUid;
        private Long amount;
        private String status;
        private String orderName;
        private String createdAt;
        private String paidAt;

        public ListDTO(Payment payment) {
            this.id = payment.getId();
            this.merchantUid = payment.getMerchantUid();
            this.amount = payment.getAmount();
            this.status = payment.getStatus().name();
            this.orderName = payment.getOrderName();
            this.createdAt = DateUtil.timestampFormat(payment.getCreatedAt());
            this.paidAt = payment.getPaidAt() != null
                    ? DateUtil.timestampFormat(payment.getPaidAt()) : null;
        }
    }

    // 결제 완료 응답
    @Data
    public static class ResponseDTO {
        private Long id;
        private String impUid;
        private String merchantUid;
        private Long amount;
        private String status;
        private String payMethod;
        private String orderName;
        private String createdAt;
        private String paidAt;

        public ResponseDTO(Payment payment) {
            this.id = payment.getId();
            this.impUid = payment.getImpUid();
            this.merchantUid = payment.getMerchantUid();
            this.amount = payment.getAmount();
            this.status = payment.getStatus().name();
            this.payMethod = payment.getPayMethod();
            this.orderName = payment.getOrderName();
            this.createdAt = DateUtil.timestampFormat(payment.getCreatedAt());
            this.paidAt = payment.getPaidAt() != null
                    ? DateUtil.timestampFormat(payment.getPaidAt()) : null;
        }
    }
}