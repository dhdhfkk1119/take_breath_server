package com.take.take_breath.members.email.controller;

import com.take.take_breath.members.MemberDTO;
import com.take.take_breath.members.email.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/emails")
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 코드 발송
    @PostMapping("/verify")
    public ResponseEntity<Void> sendCode(@Valid @RequestBody MemberDTO.SendCodeRequest req) {
        emailVerificationService.sendVerificationCode(req);
        return ResponseEntity.ok().build();
    }

    // 이메일 인증코드 검증
    @PostMapping("/verify/check")
    public ResponseEntity<MemberDTO.EmailVerificationResponse> verifyCode(
            @Valid @RequestBody MemberDTO.VerifyCodeRequest req) {

        MemberDTO.EmailVerificationResponse res = emailVerificationService.verifyCode(req);
        return ResponseEntity.ok(res);
    }
}
