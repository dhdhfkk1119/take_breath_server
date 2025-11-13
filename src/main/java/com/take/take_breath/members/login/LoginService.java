package com.take.take_breath.members.login;

import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.MemberService;
import com.take.take_breath.members.dto.MemberResponse;
import com.take.take_breath.members.login.dto.LoginResponse;
import com.take.take_breath.members.login.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginStrategyFactory loginStrategyFactory;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StateStore stateStore;

    public String getLoginPage(String provider) {

        LoginStrategy strategy = loginStrategyFactory.getStrategy(provider);

        String state = UUID.randomUUID().toString();

        stateStore.save(state);

        return strategy.getRedirectUrlWithState(state);
    }

    public LoginResponse handleCallback(String provider, String code, String state) {

        if (!stateStore.exists(state)) {
            throw new Exception403("잘못된 state 값입니다. (CSRF 방지)");
        }

        stateStore.remove(state);

        LoginStrategy strategy = loginStrategyFactory.getStrategy(provider);

        String accessToken = strategy.getAccessToken(code, state);

        UserInfo userInfo = strategy.getUserInfo(accessToken);

        Member member = memberService.loginOrSignup(userInfo);

        String jwt = jwtTokenProvider.createToken(member);

        return new LoginResponse(jwt, new MemberResponse(member));
    }
}
