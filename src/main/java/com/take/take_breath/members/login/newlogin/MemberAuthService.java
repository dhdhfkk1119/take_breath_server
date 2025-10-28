package com.take.take_breath.members.login.newlogin;

import com.take.take_breath._core._exception.*;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Member;
import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.dto.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public boolean isCheckId(String email){
        return !memberRepository.existsByEmail(email);
    }

    public MemberResponseTo.Login login(MemberRequestTo.MemberLoginRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new Exception400("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new Exception401("비밀번호가 일치하지 않습니다.");
        }

        if (!member.isEmailVerified()) {
            throw new Exception401("이메일 인증이 필요합니다.");
        }

        if (member.getStatus() == Status.PENDING) {
            throw new Exception403("관리자 승인 대기 중입니다.");
        }

        if (member.getStatus() == Status.SUSPENDED) {
            throw new Exception403("이용 정지된 계정입니다.");
        }

        String accessToken = jwtTokenProvider.createToken(member);
        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        return new MemberResponseTo.Login(
                accessToken,
                refreshToken,
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getProfileImage(),
                member.getRole().name(),
                member.getStatus().name()
        );
    }


    // 토큰 정보 등록하기
    public String refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new Exception401("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getSubject(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception400("존재하지 않는 사용자입니다."));

        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new Exception403("등록되지 않은 토큰입니다.");
        }

        return jwtTokenProvider.regenerateAccessToken(refreshToken);
    }
    
}
