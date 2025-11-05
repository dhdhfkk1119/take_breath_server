package com.take.take_breath.payment.fee;

import com.take.take_breath.members.Role;

public interface FeeStrategy {

    // 수수료 금액 계산
    Long calculateFee(Long pointAmount);

    default Long calculateFee(Long pointAmount, Role role, String transactionType) {
        return calculateFee(pointAmount);
    }

    // 수수료율 반환
    Double getFeeRate();

    // 수수료 타입
    boolean supports(String type);
}
