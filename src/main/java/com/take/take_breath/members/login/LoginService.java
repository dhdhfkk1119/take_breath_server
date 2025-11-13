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

        // 2️⃣ state 저장
        stateStore.save(state);

        // 3️⃣ provider 전략의 redirectUrl에 state 붙여서 반환
        return strategy.getRedirectUrlWithState(state);
    }

    public LoginResponse handleCallback(String provider, String code, String state) {

        // 1️⃣ state 유효성 검증
        if (!stateStore.exists(state)) {
            throw new Exception403("잘못된 state 값입니다. (CSRF 방지)");
        }

        // 2️⃣ state 제거 (재사용 방지)
        stateStore.remove(state);

        // 3️⃣ 전략 선택
        LoginStrategy strategy = loginStrategyFactory.getStrategy(provider);

        // 4️⃣ access_token 받기
        String accessToken = strategy.getAccessToken(code, state);

        // 5️⃣ 사용자 정보 조회
        UserInfo userInfo = strategy.getUserInfo(accessToken);

        // 6️⃣ 회원 생성 or 로그인
        Member member = memberService.loginOrSignup(userInfo);

        // 7️⃣ JWT 생성
        String jwt = jwtTokenProvider.createToken(member);

        return new LoginResponse(jwt, new MemberResponse(member));
    }
}
