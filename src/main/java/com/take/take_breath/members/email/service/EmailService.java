package com.take.take_breath.members.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String SUBJECT = "[TakeBreath] 이메일 인증 코드";
    private static final String BODY_PREFIX = "아래 인증 코드를 입력해 주세요.\n\n인증코드: ";

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
}