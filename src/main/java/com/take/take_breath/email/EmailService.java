package com.take.take_breath.email;

import com.take.take_breath.email.dto.EmailRequest;
import com.take.take_breath.email.dto.EmailResponse;
import org.springframework.context.ApplicationEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailCodeStore emailCodeStore;
    private final ApplicationEventPublisher publisher;


    private static final String SUBJECT = "[TakeBreath] 이메일 인증 코드";
    private static final String BODY_PREFIX = "아래 인증 코드를 입력해 주세요.\n\n인증코드: ";


    @Value("${email.code-expire-seconds:300}")
    private long expireSeconds;

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



    private static final SecureRandom random = new SecureRandom();

    // 인증 코드 발송
    @Transactional
    public void sendVerificationCode(EmailRequest req) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        send(req.getEmail(), code);
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(expireSeconds);

        emailCodeStore.save(req.getEmail(), code, expireSeconds);
    }

    // 인증 코드 검증
    @Transactional
    public EmailResponse verifyCode(EmailRequest req) {
        String storedCode = emailCodeStore.get(req.getEmail());
        boolean verified = storedCode != null && storedCode.equals(req.getCode());

        if (verified) {
            emailCodeStore.markAsVerified(req.getEmail(), expireSeconds); // 인증 완료 상태 저장
            // 이벤트 발행
            publisher.publishEvent(new EmailVerifiedEvent(this, req.getEmail()));
        }
        return new EmailResponse(verified);
    }

    public boolean isVerified(String email) {
        return emailCodeStore.isVerified(email);
    }
}