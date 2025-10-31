package com.take.take_breath.refund;

import com.take.take_breath._core._utils.DateUtil;
import lombok.Data;

public class RefundResponse {
    
    @Data
    public static class DetailDTO {
        private Long refundId;
        private Long paymentId;
        private String impUid;
        private String merchantUid;
        private Long refundAmount;
        private String status;
        private String reason;
        private String createdAt;
        private String processedAt;
        
        public DetailDTO(Refund refund) {
            this.refundId = refund.getId();
            this.paymentId = refund.getPayment().getId();
            this.impUid = refund.getPayment().getImpUid();
            this.merchantUid = refund.getPayment().getMerchantUid();
            this.refundAmount = refund.getRefundAmount();
            this.status = refund.getStatus().name();
            this.reason = refund.getReason();
            this.createdAt = DateUtil.timestampFormat(refund.getCreatedAt());
            this.processedAt = refund.getProcessedAt() != null 
                ? DateUtil.timestampFormat(refund.getProcessedAt()) : null;
        }
    }
    
    @Data
    public static class ListDTO {
        private Long refundId;
        private String impUid;
        private String merchantUid;
        private Long refundAmount;
        private String status;
        private String createdAt;
        
        public ListDTO(Refund refund) {
            this.refundId = refund.getId();
            this.impUid = refund.getPayment().getImpUid();
            this.merchantUid = refund.getPayment().getMerchantUid();
            this.refundAmount = refund.getRefundAmount();
            this.status = refund.getStatus().name();
            this.createdAt = DateUtil.timestampFormat(refund.getCreatedAt());
        }
    }
}