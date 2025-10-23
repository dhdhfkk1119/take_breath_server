package com.take.take_breath.members.service;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath.counselor.dto.CounselorRequest;
import com.take.take_breath.counselor.service.CounselorService;
import com.take.take_breath.email.EmailCodeStore;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.members.dto.MemberRequest;
import com.take.take_breath.members.entity.Member;
import com.take.take_breath.members.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailCodeStore emailCodeStore;

    // 회원가입
    @Transactional
    public Member signup(MemberRequest req) {
        if (!emailCodeStore.isVerified(req.getEmail())) {
            throw new Exception400("이메일 인증이 필요합니다.");
        }

        if (!req.isTermsService() || !req.isTermsPrivacy() || !req.isTermsThirdParty()) {
            throw new Exception400("필수 약관에 동의해야 회원가입이 가능합니다.");
        }

        String encodedPassword = passwordEncoder.encode(req.getPassword());
        Role role = (req.getRole() != null) ? req.getRole() : Role.USER;

        Status status = (role == Role.COUNSELOR)
                ? Status.PENDING // 상담사는 관리자 승인대기
                : Status.ACTIVE; // 일반 유저는 바로 활성화

        Member member = req.toEntity(req, encodedPassword, status);
        memberRepository.save(member);

        return member;
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

    // 상담사 승인
    @Transactional
    public void approveCounselor(Long memberId) {
        Member counselor = memberRepository.findById(memberId)
                .orElseThrow(() -> new Exception400("해당 상담사를 찾을 수 없습니다."));

        if(counselor.getRole() != Role.COUNSELOR) {
            throw new IllegalArgumentException("상담사 계정만 승인할 수 있습니다.");
        }

        if(counselor.getStatus() !=Status.PENDING) {
            throw new IllegalArgumentException("이미 승인된 상담사 입니다.");
        }

        counselor.setStatus(Status.ACTIVE);
        log.info("상담사 승인 완료: {}", counselor.getEmail());
    }



}

