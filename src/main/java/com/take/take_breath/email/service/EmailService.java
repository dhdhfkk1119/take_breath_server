package com.take.take_breath.email.service;

import com.take.take_breath.members.dto.request.EmailRequest;
import com.take.take_breath.members.dto.response.EmailResponse;
import com.take.take_breath.email.EmailCodeStore;
import org.springframework.context.ApplicationEventPublisher;
import com.take.take_breath.email.event.EmailVerifiedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailCodeStore emailCodeStore;
    private final ApplicationEventPublisher publisher;


    private static final String SUBJECT = "[TakeBreath] 이메일 인증 코드";
    private static final String BODY_PREFIX = "아래 인증 코드를 입력해 주세요.\n\n인증코드: ";


    // 실제 메일 발송
    public void send(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(SUBJECT);
            message.setText(BODY_PREFIX + code);
            mailSender.send(message);
            log.info("이메일 발송 성공: {}", to);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new IllegalStateException("이메일 발송 중 오류 발생");
        }
    }

    @Value("${email.code-expire-seconds:300}")
    private long expireSeconds;

    private static final SecureRandom random = new SecureRandom();

    // 인증 코드 발송
    @Transactional
    public void sendVerificationCode(EmailRequest req) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        send(req.getEmail(), code);
        emailCodeStore.save(req.getEmail(), code, expireSeconds);
    }

    // 인증 코드 검증
    @Transactional
    public EmailResponse verifyCode(EmailRequest req) {
        String storedCode = emailCodeStore.get(req.getEmail());
        boolean verified = storedCode != null && storedCode.equals(req.getCode());

        if (verified) {
            emailCodeStore.delete(req.getEmail()); // 인증 성공 시 코드 삭제

            // 이벤트 발행
            publisher.publishEvent(new EmailVerifiedEvent(this, req.getEmail()));
        }
        return new EmailResponse(verified);
    }
}