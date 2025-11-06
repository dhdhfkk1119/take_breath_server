package com.take.take_breath.payment.fee;

public class FixedFeeStrategy implements FeeStrategy {

    // 고정 수수료 금액
    private static final Long FIXED_FEE_AMOUNT = 3000L;

    @Override
    public Long calculateFee(Long pointAmount) {
        return FIXED_FEE_AMOUNT;
    }

    @Override
    public Double getFeeRate() {
        // 고정 금액이므로 비율은 0.0 반환
        return 0.0;
    }

    @Override
    public boolean supports(String type) {
        return "FIXED".equalsIgnoreCase(type);
    }
}
