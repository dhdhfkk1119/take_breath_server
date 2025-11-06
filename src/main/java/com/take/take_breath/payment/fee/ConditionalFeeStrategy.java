package com.take.take_breath.payment.fee;

import com.take.take_breath.members.Role;
import org.springframework.stereotype.Component;

@Component
public class ConditionalFeeStrategy implements FeeStrategy {

    private static final double USER_CHARGE_FEE = 0.1;
    private static final double COUNSELOR_WITHDRAW_FEE = 0.3;

    @Override
    public Long calculateFee(Long pointAmount) {
         return Math.round(pointAmount * 0.1);
    }

    @Override
    public Long calculateFee(Long pointAmount, Role role, String transactionType) {
        if (role == Role.COUNSELOR && "WITHDRAW".equals(transactionType)) {
            return Math.round(pointAmount * COUNSELOR_WITHDRAW_FEE);
        }
        return Math.round(pointAmount * USER_CHARGE_FEE);
    }

    @Override
    public Double getFeeRate() {
        return USER_CHARGE_FEE;
    }

    @Override
    public boolean supports(String type) {
        return "CONDITIONAL".equalsIgnoreCase(type);
    }
}

/**
 * FeeStrategy strategy = new ConditionalFeeStrategy();
 *
 * strategy.calculateFee(10000L);  // 1000 (기본)
 * strategy.calculateFee(10000L, Role.USER, "CHARGE");
 * strategy.calculateFee(10000L, Role.COUNSELOR, "WITHDRAW");
 */
