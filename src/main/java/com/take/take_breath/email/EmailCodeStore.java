package com.take.take_breath.email;

public interface EmailCodeStore {
    void save(String email, String code, long expireSeconds);
    String get(String email);
    void delete(String email);

    // 이메일 인증 완료 상태 저장
    void markAsVerified(String email, long expireSeconds);

    // 이메일 인증 완료 여부 확인
    boolean isVerified(String email);
}