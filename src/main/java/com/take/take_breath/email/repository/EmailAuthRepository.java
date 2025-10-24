package com.take.take_breath.email.repository;

import com.take.take_breath.email.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByEmailAndVerifiedIsTrue(String email);
}
