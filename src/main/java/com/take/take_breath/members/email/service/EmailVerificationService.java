package com.take.take_breath.members.email.service;

import com.take.take_breath.members.MemberRepository;
import com.take.take_breath.members.dto.request.EmailSendCodeRequest;
import com.take.take_breath.members.dto.request.EmailVerifyCodeRequest;
import com.take.take_breath.members.dto.response.EmailVerificationResponse;
import com.take.take_breath.members.email.EmailCodeStore;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailService emailService;
    private final EmailCodeStore emailCodeStore;
    private final MemberRepository memberRepository;

    @Value("${email.code-expire-seconds:300}")
    private long expireSeconds;

    private static final SecureRandom random = new SecureRandom();

    // 인증 코드 발송
    @Transactional
    public void sendVerificationCode(EmailSendCodeRequest req) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        emailService.send(req.getEmail(), code);
        emailCodeStore.save(req.getEmail(), code, expireSeconds);
    }

    // 인증 코드 검증
    @Transactional
    public EmailVerificationResponse verifyCode(EmailVerifyCodeRequest req) {
        String storedCode = emailCodeStore.get(req.getEmail());
        boolean verified = storedCode != null && storedCode.equals(req.getCode());

        if (verified) {
            emailCodeStore.delete(req.getEmail()); // 인증 성공 시 코드 삭제
            memberRepository.findByEmail(req.getEmail())
                    .ifPresent(member -> member.setEmailVerified(true));
        }
        return new EmailVerificationResponse(verified);
    }
}
