package com.take.take_breath.members.email.controller;

import com.take.take_breath.members.dto.request.EmailRequest;
import com.take.take_breath.members.dto.response.EmailResponse;
import com.take.take_breath.members.email.service.EmailService;
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

    private final EmailService emailService;

    // 이메일 인증 코드 발송
    @PostMapping("/verify")
    public ResponseEntity<?> sendCode(@Valid @RequestBody EmailRequest req) {
        emailService.sendVerificationCode(req);
        return ResponseEntity.ok().build();
    }

    // 이메일 인증코드 검증
    @PostMapping("/verify/check")
    public ResponseEntity<?> verifyCode(
            @Valid @RequestBody EmailRequest req) {

        EmailResponse res = emailService.verifyCode(req);
        return ResponseEntity.ok(res);
    }
}
