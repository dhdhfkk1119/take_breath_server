package com.take.take_breath.members;

import com.take.take_breath._core._exception.Exception400;
import com.take.take_breath._core._exception.Exception401;
import com.take.take_breath._core._exception.Exception403;
import com.take.take_breath._core._jwt.JwtTokenProvider;
import com.take.take_breath._core._utils.UploadProperties;
import com.take.take_breath.counselor.CounselorRepository;
import com.take.take_breath.email.EmailCodeStore;
import com.take.take_breath.email.EmailService;
import com.take.take_breath.email.dto.EmailRequest;
import com.take.take_breath.email.dto.EmailResponse;
import com.take.take_breath.members.dto.*;
import com.take.take_breath.members.dto.MemberRequestTo;
import com.take.take_breath.members.dto.MemberResponseTo;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private final MemberWithdrawalService memberWithdrawalService;


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
            LocalDateTime suspendedUntil = member.getSuspendedUntil();
            long daysLeft = 0;

            if(suspendedUntil !=null) {
                LocalDate today = LocalDate.now();
                LocalDate endDate = suspendedUntil.toLocalDate();

                if (today.isBefore(endDate)) {
                    daysLeft = ChronoUnit.DAYS.between(today, endDate);
                }
            }

            throw new Exception403("이용 정지된 계정입니다. 정지 해제까지 " + daysLeft + "일 남았습니다.");
        }

        // 항상 Access Token은 발급
        String accessToken = jwtTokenProvider.createToken(member);
        String refreshToken = null;

        if (member.getStatus() == Status.WITHDRAWAL) {
            // 탈퇴 처리 중인 계정의 경우, 최소 정보 (AccessToken, Status, 남은 일수)만 반환
            long daysLeft = memberWithdrawalService.getDaysUntilDeletion(member);
            return new MemberResponseTo.Login(accessToken, member.getStatus().name(), daysLeft);
        }

        // 자동 로그인일 경우 Refresh Token 발급 및 저장
        if (req.isAutoLogin()) {
            refreshToken = jwtTokenProvider.createRefreshToken(member);
            member.setRefreshToken(refreshToken);
            // memberRepository.save(member)는 로직이 끝난 후 한 번만 호출하는 것이 효율적일 수 있습니다.
        } else {
            // 일반 로그인일 경우 기존 refreshToken 제거
            member.setRefreshToken(null);
        }

        memberRepository.save(member); // Refresh Token 변경사항 저장

        // 일반 로그인 (Status.ACTIVE 등)
        return new MemberResponseTo.Login(
                accessToken,
                refreshToken, // autoLogin=false면 null일 수 있음
                member.getId(),
                member.getName(),
                member.getNickName(),
                member.getEmail(),
                member.getProfileImage(),
                member.getRole().name(),
                member.getStatus().name(),
                member.getPhone()
                // daysLeft는 DTO 생성자에서 null로 처리됨
        );
    }

    public MemberResponseTo.isCheckEmailDTO checkEmail(String email) {
        boolean isExist = memberRepository.existsByEmail(email);

        MemberResponseTo.isCheckEmailDTO responseDTO = new MemberResponseTo.isCheckEmailDTO();

        if (isExist) {
            responseDTO.setMessage("이미 존재하는 이메일입니다.");
            responseDTO.setCheck(true); // 존재함 (true)
        } else {
            responseDTO.setMessage("사용 가능한 이메일입니다.");
            responseDTO.setCheck(false); // 존재하지 않음 (false)
        }

        return responseDTO;
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
    public void  updateMemberInfo (String email, String nickName, MultipartFile image) throws IOException {
        System.out.println("updateMemberInfo 호출됨: nickName=" + nickName);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception400("회원 정보를 찾을 수 없습니다."));

        // 닉네임 수정
        if (nickName != null && !nickName.isEmpty()) {
            member.setNickName(nickName);
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

