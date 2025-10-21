package com.take.take_breath.members;

import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.email.EmailCodeStore;
import com.take.take_breath.members.email.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailCodeStore emailCodeStore;

    // 회원가입
    @Transactional
    public void signup(MemberDTO.SignupRequest req) {
        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role(Role.USER)
                .status(Status.PENDING)
                .emailVerified(false)
                .build();
        memberRepository.save(member);

    }

    // 로그인
    public String login(MemberDTO.LoginRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        if (!member.isEmailVerified()) {
            throw new IllegalStateException("이메일 인증이 필요합니다.");
        }

        if (member.getStatus() == Status.PENDING) {
            throw new IllegalStateException("관리자 승인 대기 중입니다.");
        }

        if (member.getStatus() == Status.SUSPENDED) {
            throw new IllegalStateException("이용 정지된 계정입니다.");
        }

        return jwtTokenProvider.createToken(member);
    }


}

