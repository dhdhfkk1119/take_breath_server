package com.take.take_breath.email.listener;

import com.take.take_breath.members.dto.request.EmailRequest;
import com.take.take_breath.email.service.EmailService;
import com.take.take_breath.members.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @EventListener
    public void handleMemberRegistered(MemberRegisteredEvent event) {
        String email = event.getMember().getEmail();

        // 회원가입 후 이메일 인증 코드 발송
        emailService.sendVerificationCode(
                EmailRequest.builder().email(email).build()
        );
        log.info("회원가입 이벤트 수신 → 인증 메일 발송 완료: {}", email);
    }
}