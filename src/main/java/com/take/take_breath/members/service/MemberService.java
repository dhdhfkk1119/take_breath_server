package com.take.take_breath.members.service;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.dto.request.MemberRequest;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.event.MemberRegisteredEvent;
import com.take.take_breath.members.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;


    // 회원가입
    @Transactional
    public void signup(MemberRequest req) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        Member member = Member.builder()
                .email(req.getEmail())
                .password(encodedPassword)
                .name(req.getName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        // 회원가입 성공 이벤트 발행
        log.info("[이벤트 발행] MemberRegisteredEvent -> {}", member.getEmail());
        publisher.publishEvent(new MemberRegisteredEvent(this, member, member.getEmail()));
    }


    // 로그인
    public String login(MemberRequest req) {
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

        String token = jwtTokenProvider.createToken(member);
        return jwtTokenProvider.createToken(member);
    }



}

