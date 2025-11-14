package com.take.take_breath.members.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginStrategyFactory {

    private final List<LoginStrategy> loginStrategies;

    public LoginStrategy getStrategy(String provider) {
        return loginStrategies.stream()
                .filter(s -> s.getProvider().equals(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 provider입니다."));
    }
}
