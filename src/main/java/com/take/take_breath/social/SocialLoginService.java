package com.take.take_breath.social;

import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.*;
import com.take.take_breath.members.dto.MemberResponse;
import com.take.take_breath.members.login.dto.LoginResponse;
import com.take.take_breath.members.login.dto.UserInfo;
import com.take.take_breath.social.naver.NaverVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginService {

    private final NaverVerifier naverVerifier;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse loginOrSignup(SocialLoginRequest request) {

        UserInfo userInfo = naverVerifier.verify(request.getIdToken());

        log.info("서비스 소셜 로그인 API : {} : {} " , request.getIdToken(),request.getProvider());

        // provider + socialId 로 회원 조회
        Member member = memberRepository
                .findByProviderAndSocialId(userInfo.getProvider(), userInfo.getSocialId())
                .orElseGet(() -> createSocialMember(userInfo));

        return generateJwtResponse(member);
    }


    private Member createSocialMember(UserInfo userInfo) {
        Member newMember = Member.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName() != null ? userInfo.getName() : "소셜 사용자")
                .nickName(userInfo.getName() != null ? userInfo.getName() : "소셜 사용자")
                .provider(userInfo.getProvider())          // "naver" or "google"
                .socialId(userInfo.getSocialId())          // 구글/네이버 고유 ID
                .loginType(userInfo.getProvider().equals("naver") ? LoginType.NAVER : LoginType.GOOGLE)
                .password("")                              // 소셜 로그인은 비밀번호 없음
                .profileImage(
                        userInfo.getProfileImage() != null && !userInfo.getProfileImage().isEmpty()
                                ? userInfo.getProfileImage()
                                : "/default/profile.png"
                )
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();

        return memberRepository.save(newMember);
    }


    private LoginResponse generateJwtResponse(Member member) {
        String jwt = jwtTokenProvider.createToken(member);
        MemberResponse memberResponse = new MemberResponse(member);
        return new LoginResponse(jwt, memberResponse);
    }
}
