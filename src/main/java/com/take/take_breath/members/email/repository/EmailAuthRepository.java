package com.take.take_breath.members.email.repository;

import com.take.take_breath.members.email.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findByEmail(String email);
    void deleteByEmail(String email);
}
