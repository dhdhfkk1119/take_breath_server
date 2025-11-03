package com.take.take_breath.refund;

import lombok.Data;

public class RefundRequest {
    
    @Data
    public static class CreateDTO {
        private Long paymentId;    // 환불할 결제 ID
        private String reason;     // 환불 사유
    }
}