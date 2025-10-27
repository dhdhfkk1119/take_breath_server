package com.take.take_breath.members;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath._core._utils.UploadProperties;
import com.take.take_breath.email.EmailCodeStore;
import com.take.take_breath.email.EmailService;
import com.take.take_breath.email.dto.EmailRequest;
import com.take.take_breath.email.dto.EmailResponse;
import com.take.take_breath.members.dto.*;
import com.take.take_breath.terms.dto.MemberTermsRequest;
import com.take.take_breath.terms.MemberTerms;
import com.take.take_breath.terms.Terms;
import com.take.take_breath.terms.MemberTermsRepository;
import com.take.take_breath.terms.TermsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.take.take_breath._core._utils.UploadFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailCodeStore emailCodeStore;
    private final MemberTermsRepository memberTermsRepository;
    private final TermsRepository termsRepository;
    private final EmailService emailService;
    private final UploadFile uploadFile;
    private final UploadProperties uploadProperties;



    // 회원가입
    @Transactional
    public Member signup(MemberRequest req) {
        if (!emailCodeStore.isVerified(req.getEmail())) {
            throw new Exception400("이메일 인증이 필요합니다.");
        }

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.");
        }

        // 필수약관 하나라도 미동의시 예외 발생
        boolean allRequiredAgreed = req.getAgreements().stream()
                .filter(request -> {
                    Terms terms = termsRepository.findById(request.getTermsId())
                            .orElseThrow(() -> new Exception400("존재하지 않는 약관입니다."));
                    return terms.isRequired(); // 필수 약관만 필터링
                })
                .allMatch(request -> request.isAgreed());


        if (!allRequiredAgreed) {
            throw new Exception400("필수 약관에 모두 동의해야 회원가입이 가능합니다.");
        }

        String encodedPassword = passwordEncoder.encode(req.getPassword());

        Role role = (req.getRole() != null) ? req.getRole() : Role.USER;
        Status status = (role == Role.COUNSELOR)
                ? Status.PENDING // 상담사는 관리자 승인대기
                : Status.ACTIVE; // 일반 유저는 바로 활성화

        // 회원 저장
        Member member = req.toEntity(req, encodedPassword, status);
        memberRepository.save(member);


        // 약관 동의 저장
        for (MemberTermsRequest agreement : req.getAgreements()) {
            Terms terms = termsRepository.findById(agreement.getTermsId())
                    .orElseThrow(() -> new Exception400("존재하지 않는 약관입니다."));
            MemberTerms memberTerms = agreement.toEntity(member, terms);
            memberTermsRepository.save(memberTerms);
        }

        return member;
    }


    // 로그인
    public String login(MemberRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new Exception400("존재하지 않는 이메일입니다."));

        System.out.println(">>> PasswordEncoder Bean Type = " + passwordEncoder.getClass().getName());

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

    // 이메일 찾기
    public MemberEmailResponse findEmail(MemberFindEmailRequest req) {
        Member member = memberRepository.findByNameAndPhone(req.getName(), req.getPhone())
                .orElseThrow(() -> new Exception400("일치하는 회원이 없습니다."));
        return new MemberEmailResponse(member.getEmail());
    }

    // 비밀번호 재설정
    @Transactional
    public void resetPassword(PasswordResetRequest req) {
        // 1. 이메일 인증 코드 검증
        EmailRequest emailReq = new EmailRequest(req.getEmail(), req.getCode());
        EmailResponse emailRes = emailService.verifyCode(emailReq);

        if (!emailRes.isEmailVerified()) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        // 2. 회원 조회
        Member member = memberRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다."));

        // 3. 비밀번호 변경
        member.setPassword(passwordEncoder.encode(req.getNewPassword()));
    }

    // 회원정보 조회
    public MemberResponse getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception400("회원 정보를 찾을 수 없습니다."));

        return new MemberResponse(member);
    }

    // 회원정보 수정
    @Transactional
    public void  updateMemberInfo (String email, String nickname, MultipartFile image) throws IOException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception400("회원 정보를 찾을 수 없습니다."));

        // 닉네임 수정
        if (nickname != null && !nickname.isEmpty()) {
            member.setNickname(nickname);
        }

        // 프로필 이미지 수정
        if (image != null && !image.isEmpty()) {
            // 기존 이미지 삭제 (기본이미지면 건너뜀)
            uploadFile.deleteProfileImage(member.getProfileImage(), "member");

            // 새 이미지 업로드
            String uploadedPath = uploadFile.uploadImage(image, "member");
            member.setProfileImage(uploadedPath);
        }
    }

    // 회원탈퇴
    @Transactional
    public void deleteMember(String email) throws IOException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception400("존재하지 않는 회원입니다."));

        // 기본 이미지가 아닐 경우 실제 이미지 파일 삭제
        uploadFile.deleteProfileImage(member.getProfileImage(), uploadProperties.getMemberDir());

        // 회원 데이터 삭제
        memberRepository.delete(member);
    }


}

