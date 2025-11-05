package com.take.take_breath.payment.fee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeeStrategyFactory {

    private final List<FeeStrategy> feeStrategies;

    public FeeStrategy findStrategy(String type) {
        for (FeeStrategy strategy : feeStrategies) {
            if (strategy.supports(type)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 수수료 계산 방식입니다: " + type);
    }

    public FeeStrategy getDefaultStrategy() {
        return findStrategy("CONDITIONAL");
    }
}






