package com.take.take_breath.members;


import com.take.take_breath.email.EmailService;
import com.take.take_breath.email.dto.EmailRequest;
import com.take.take_breath.members.dto.MemberEmailResponse;
import com.take.take_breath.members.dto.MemberFindEmailRequest;
import com.take.take_breath.members.dto.MemberRequest;
import com.take.take_breath.members.dto.PasswordResetRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;


    // 일반회원 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequest req) {
        req.setRole(Role.USER);
        memberService.signup(req);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody MemberRequest req) {
        String token = memberService.login(req);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
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
}