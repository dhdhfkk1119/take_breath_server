package com.take.take_breath.members.listener;

import com.take.take_breath.members.repository.MemberRepository;
import com.take.take_breath.members.email.event.EmailVerifiedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventListener {

    private final MemberRepository memberRepository;

    @EventListener
    @Transactional
    public void handleEmailVerified(EmailVerifiedEvent event) {
        String email = event.getEmail();
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    member.setEmailVerified(true);
                    log.info("회원 이메일 인증 완료: {}", email);
                });
    }
}