package com.take.take_breath.members.email;


import com.take.take_breath.members.email.entity.EmailAuth;
import com.take.take_breath.members.email.repository.EmailAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Primary // 이걸 붙여야 DB 버전이 EmailVerificationService에 자동 주입됨
@RequiredArgsConstructor
public class DbEmailCodeStore implements EmailCodeStore {

    private final EmailAuthRepository emailAuthRepository;

    @Override
    public void save(String email, String code, long expireSeconds) {
        emailAuthRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.isExpired()) {
                throw new IllegalStateException("이미 인증 코드가 발송되었습니다. 잠시 후 다시 시도해주세요.");
            }
            emailAuthRepository.delete(existing);
        });

        LocalDateTime expireAt = LocalDateTime.now().plusSeconds(expireSeconds);

        // 기존 코드가 있으면 삭제 후 새로 저장
        emailAuthRepository.findByEmail(email)
                .ifPresent(auth -> emailAuthRepository.delete(auth));

        emailAuthRepository.save(EmailAuth.builder()
                .email(email)
                .code(code)
                .expireAt(expireAt)
                .build());
    }

    @Override
    public String get(String email) {
        return emailAuthRepository.findByEmail(email)
                .filter(auth -> !auth.isExpired()) // 만료 안 됐으면
                .map(auth -> auth.getCode())
                .orElseThrow(() -> new IllegalArgumentException("인증코드가 만료되었습니다."));
    }

    @Override
    public void delete(String email) {
        emailAuthRepository.deleteByEmail(email);
    }
}