package com.take.take_breath.members;


import com.take.take_breath.email.EmailService;
import com.take.take_breath.email.dto.EmailRequest;
import com.take.take_breath.members.dto.*;
import com.take.take_breath.members.login.newlogin.MemberRequestTo;
import com.take.take_breath.members.login.newlogin.MemberResponseTo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final MemberWithdrawalService memberWithdrawalService;


    // 일반회원 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequest req) {
        req.setRole(Role.USER);
        memberService.signup(req);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody MemberRequestTo.MemberLoginRequest req) {
        MemberResponseTo.Login response = memberService.login(req);

        log.info("로그인 컨트롤러 : {}",response);
        // 기본 헤더 세팅
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getAccessToken());

        // autoLogin이 true일 때만 Refresh Token 헤더 추가
        if (req.isAutoLogin()) {
            builder.header("Refresh-Token", response.getRefreshToken());
        }

        return builder.body(response);
    }


    // 이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestBody MemberFindEmailRequest req) {
        MemberEmailResponse response = memberService.findEmail(req);
        return ResponseEntity.ok(response);
    }

    // 비밀번호 인증 코드 전송
    @PostMapping("/password/reset-request")
    public ResponseEntity<String> sendResetCode(@RequestBody EmailRequest req) {
        emailService.sendVerificationCode(req); // 기존 메서드 재사용
        return ResponseEntity.ok("비밀번호 재설정용 인증 코드가 이메일로 발송되었습니다.");
    }

    // 비밀번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest req) {
        memberService.resetPassword(req);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // 회원정보 불러오기
    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo(HttpServletRequest req) {
        String email = (String) req.getAttribute("memberEmail");
        MemberResponse response = memberService.getMemberInfo(email);
        return ResponseEntity.ok(response);
    }

    // 회원정보 수정
    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(
            HttpServletRequest request,
            @RequestPart(value = "nickname", required = false) String nickname,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        String email = (String) request.getAttribute("memberEmail");
        memberService.updateMemberInfo(email, nickname, image);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 회원 탈퇴 요청
    @PostMapping("/withdrawal")
    public ResponseEntity<?> requestWithdrawal(
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, String> body) {

        String email = (String) request.getAttribute("memberEmail");
        String reason = (body != null && body.containsKey("reason")) ? body.get("reason") : "";

        memberWithdrawalService.requestWithdrawal(email, reason);
        return ResponseEntity.ok("탈퇴 요청이 완료되었습니다. 3개월 후 자동으로 삭제됩니다.");
    }

    // 탈퇴 취소
    @PostMapping("/cancel-withdrawal")
    public ResponseEntity<?> cancelWithdrawal(HttpServletRequest request) {
        String email = (String) request.getAttribute("memberEmail");
        memberWithdrawalService.cancelWithdrawal(email);
        return ResponseEntity.ok("계정이 복구되었습니다.");
    }



}