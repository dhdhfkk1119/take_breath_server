package com.take.take_breath.members.email;

public interface EmailCodeStore {
    void save(String email, String code, long expireSeconds);
    String get(String email);
    void delete(String email);
}